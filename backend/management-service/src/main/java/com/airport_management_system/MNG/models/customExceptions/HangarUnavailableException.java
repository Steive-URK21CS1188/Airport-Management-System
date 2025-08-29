package com.airport_management_system.MNG.models.customExceptions;

public class HangarUnavailableException extends RuntimeException {
    public HangarUnavailableException(String message) {
        super(message);
    }
}
