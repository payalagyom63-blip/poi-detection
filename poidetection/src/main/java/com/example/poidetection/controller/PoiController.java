package com.example.poidetection.controller;

import com.example.poidetection.dto.PoiResult;
import com.example.poidetection.dto.LocationRequestDTO;
import com.example.poidetection.service.PoiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/location")
public class PoiController {

    @Autowired
    private PoiService poiService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Called from frontend every few seconds
    @PostMapping("/update")
    public String updateLocation(@RequestBody LocationRequestDTO request) {

        double lat = request.getLatitude();
        double lon = request.getLongitude();

        Optional<PoiResult> poi = poiService.findNearestPOI(lat, lon);

        if (poi.isPresent()) {
            String message = "Welcome to " + poi.get().getName();

            // send to frontend (WebSocket)
            messagingTemplate.convertAndSend("/topic/poi", message);

            // show in IntelliJ console
            System.out.println("🔔🔔" + message);

            return "User entered POI: " + poi.get().getName();
        }

        return "No POI nearby";
    }
}