package com.example.poidetection.dto;

import jakarta.validation.constraints.NotNull;

public class LocationRequestDTO {

    @NotNull
    private Long userId;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    // getters
    public Long getUserId() {
        return userId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    // setters
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}