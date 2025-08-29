package com.airport_management_system.AMS.models.customExceptions;

public class PilotUnavailableException extends RuntimeException {
    public PilotUnavailableException(String message) {
        super(message);
    }
}
