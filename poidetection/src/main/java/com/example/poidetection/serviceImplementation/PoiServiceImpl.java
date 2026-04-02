package com.example.poidetection.serviceImplementation;

import com.example.poidetection.dto.PoiResult;
import com.example.poidetection.service.PoiService;
import com.example.poidetection.util.DistanceCalculator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class PoiServiceImpl implements PoiService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final int SEARCH_RADIUS = 500;
    private static final int MATCH_RADIUS = 100;
    private static final int MAX_RETRIES = 3;

    @Override
    public Optional<PoiResult> findNearestPOI(double latitude, double longitude) {

        // TRY OVERPASS API WITH RETRY
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                System.out.println("Overpass attempt: " + attempt);

                String url = "https://overpass-api.de/api/interpreter?data=" +
                        "[out:json];node(around:" + SEARCH_RADIUS + "," + latitude + "," + longitude + ")" +
                        "[amenity~\"restaurant|fuel|shopping_mall\"];out;";

                String response = restTemplate.getForObject(url, String.class);

                if (response == null || response.isEmpty()) continue;

                JsonNode root = objectMapper.readTree(response);
                JsonNode elements = root.path("elements");

                if (!elements.isArray() || elements.size() == 0) continue;

                Optional<PoiResult> result = processPOIs(elements, latitude, longitude);

                if (result.isPresent()) return result;

            } catch (Exception e) {
                System.out.println("Overpass failed (attempt " + attempt + "): " + e.getMessage());
            }
        }

        // FALLBACK → NOMINATIM API
        try {
            System.out.println("Switching to fallback API (Nominatim)");

            String url = "https://nominatim.openstreetmap.org/search?format=json" +
                    "&q=restaurant" +
                    "&limit=5" +
                    "&lat=" + latitude +
                    "&lon=" + longitude;

            String response = restTemplate.getForObject(url, String.class);

            if (response == null || response.isEmpty()) return Optional.empty();

            JsonNode array = objectMapper.readTree(response);

            if (!array.isArray() || array.size() == 0) return Optional.empty();

            JsonNode first = array.get(0);

            double poiLat = first.path("lat").asDouble();
            double poiLon = first.path("lon").asDouble();
            String name = first.path("display_name").asText("Unknown");
            String type = "fallback";

            double distance = DistanceCalculator.calculate(latitude, longitude, poiLat, poiLon);

            if (distance <= MATCH_RADIUS) {
                System.out.println("Fallback POI: " + name);
                return Optional.of(new PoiResult(name, type, poiLat, poiLon));
            }

        } catch (Exception e) {
            System.out.println("Fallback API failed: " + e.getMessage());
        }

        return Optional.empty();
    }

    //  HELPER METHOD
    private Optional<PoiResult> processPOIs(JsonNode elements, double lat, double lon) {

        PoiResult nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (JsonNode poiNode : elements) {

            double poiLat = poiNode.path("lat").asDouble();
            double poiLon = poiNode.path("lon").asDouble();

            double distance = DistanceCalculator.calculate(lat, lon, poiLat, poiLon);

            JsonNode tags = poiNode.path("tags");

            String name = tags.has("name")
                    ? tags.get("name").asText()
                    : "Unknown Place";

            String type = tags.has("amenity")
                    ? tags.get("amenity").asText()
                    : "Unknown";

            if (distance < minDistance) {
                minDistance = distance;
                nearest = new PoiResult(name, type, poiLat, poiLon);
            }
        }

        if (nearest != null && minDistance <= MATCH_RADIUS) {
            System.out.println("Nearest POI: " + nearest.getName() +
                    " | Distance: " + minDistance + " meters");

            return Optional.of(nearest);
        }

        return Optional.empty();
    }
}