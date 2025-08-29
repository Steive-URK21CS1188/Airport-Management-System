package com.airport_management_system.MNG.models.customExceptions;

public class PilotUnavailableException extends RuntimeException {
    public PilotUnavailableException(String message) {
        super(message);
    }
}
