package com.example.poidetection.serviceImplementation;

import com.example.poidetection.dto.PoiResult;
import com.example.poidetection.service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Basic notification
    @Override
    public void sendNotification(String message) {
        messagingTemplate.convertAndSend("/topic/poi", message);
        System.out.println("🔔 Notification sent: " + message);
    }

    // POI-based notification
    @Override
    public void sendPoiNotification(PoiResult poiResult) {

        String message = "Welcome to " + poiResult.getName() +
                " (" + poiResult.getType() + ") 🎉";

        messagingTemplate.convertAndSend("/topic/poi", message);

        System.out.println("🔔 POI Notification: " + message);
    }
}