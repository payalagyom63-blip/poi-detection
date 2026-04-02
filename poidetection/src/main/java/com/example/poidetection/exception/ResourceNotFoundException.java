package com.example.poidetection.exception;

public class ResourceNotFoundException extends RuntimeException {

    // here its a Constructor
    public ResourceNotFoundException(String message) {
        super(message);
    }
}