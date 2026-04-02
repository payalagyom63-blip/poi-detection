package com.example.poidetection.service;

import com.example.poidetection.dto.PoiResult;

public interface NotificationService {

    //  Basic notification
    void sendNotification(String message);

    //  POI-based notification (IMPORTANT)
    void sendPoiNotification(PoiResult poiResult);
}