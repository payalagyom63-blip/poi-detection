package com.example.poidetection.dto;

public class PoiResult {

    private String name;
    private String type;
    private double latitude;
    private double longitude;

    public PoiResult(String name, String type, double latitude, double longitude) {
        this.name = name;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}