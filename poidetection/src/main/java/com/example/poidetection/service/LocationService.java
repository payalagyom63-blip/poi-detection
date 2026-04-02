package com.example.poidetection.service;

import com.example.poidetection.dto.LocationRequestDTO;

public interface LocationService {
        boolean processLocation(LocationRequestDTO dto);
}