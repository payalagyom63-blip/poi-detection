package com.example.poidetection.service;

import com.example.poidetection.dto.PoiResult;

import java.util.Optional;
//changed
public interface PoiService {
    Optional<PoiResult> findNearestPOI(double latitude, double longitude);
}