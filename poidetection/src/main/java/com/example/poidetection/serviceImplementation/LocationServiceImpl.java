package com.example.poidetection.serviceImplementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.poidetection.dto.LocationRequestDTO;
import com.example.poidetection.dto.PoiResult;
import com.example.poidetection.entity.Location;
import com.example.poidetection.entity.User;
import com.example.poidetection.exception.ResourceNotFoundException;
import com.example.poidetection.repository.LocationRepository;
import com.example.poidetection.repository.UserRepository;
import com.example.poidetection.service.LocationService;
import com.example.poidetection.service.NotificationService;
import com.example.poidetection.service.PoiService;
import com.example.poidetection.util.DistanceCalculator;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private PoiService poiService;

    @Autowired
    private NotificationService notificationService;

    private static final double MIN_DISTANCE = 30; // reduced for testing

    @Override
    public boolean processLocation(LocationRequestDTO dto) {

        System.out.println("Incoming location: " + dto.getLatitude() + ", " + dto.getLongitude());

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check consent
        if (!user.isLocationTrackingEnabled()) {
            System.out.println(" Tracking disabled for user");
            return false;
        }

        // 🔹 Movement check
        Location lastLocation =
                locationRepository.findTopByUserOrderByTimestampDesc(user);

        if (lastLocation != null) {
            double distance = DistanceCalculator.calculate(
                    lastLocation.getLatitude(),
                    lastLocation.getLongitude(),
                    dto.getLatitude(),
                    dto.getLongitude()
            );

            System.out.println(" Distance moved: " + distance + " meters");

            if (distance < MIN_DISTANCE) {
                System.out.println(" Skipping (user not moved enough)");
                return false;
            }
        }

        // Save location
        Location location = new Location();
        location.setLatitude(dto.getLatitude());
        location.setLongitude(dto.getLongitude());
        location.setTimestamp(LocalDateTime.now());
        location.setUser(user);
        locationRepository.save(location);

        System.out.println(" Location saved");

        //  POI detection
        Optional<PoiResult> poiOpt;
        try {
            poiOpt = poiService.findNearestPOI(
                    dto.getLatitude(),
                    dto.getLongitude()
            );
        } catch (Exception e) {
            System.out.println("POI API failed: " + e.getMessage());
            return false;
        }

        // ENTRY detection
        if (poiOpt.isPresent()) {
            PoiResult poi = poiOpt.get();

            System.out.println(" POI detected: " + poi.getName());

            // IMPORTANT LOGIC
            if (user.getLastVisitedPoi() == null ||
                    !user.getLastVisitedPoi().equalsIgnoreCase(poi.getName())) {

                String message = "Welcome to " + poi.getName();

                System.out.println("🔔 " + message); //  WILL SHOW IN INTELLIJ

                notificationService.sendPoiNotification(poi);

                user.setLastVisitedPoi(poi.getName());
                userRepository.save(user);

                return true;
            } else {
                System.out.println("Same POI, no new notification");
            }
        }

        // EXIT detection
        if (poiOpt.isEmpty() && user.getLastVisitedPoi() != null) {

            String exitMessage = "Exited POI: " + user.getLastVisitedPoi();

            System.out.println("🚪 " + exitMessage);

            notificationService.sendNotification(exitMessage);

            user.setLastVisitedPoi(null);
            userRepository.save(user);
        }

        return false;
    }
}