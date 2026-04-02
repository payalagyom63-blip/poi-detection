package com.example.poidetection.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.poidetection.dto.LocationRequestDTO;
import com.example.poidetection.service.LocationService;

@RestController
@RequestMapping("/location")
public class LocationController {

    @Autowired
    private LocationService locationService;

//    @PostMapping(value = "/update", produces = "application/json")
//    public ResponseEntity<?> updateLocation(@RequestBody LocationRequestDTO dto) {
//
//        boolean result = locationService.processLocation(dto);
//        return ResponseEntity.ok(result);
//    }
}