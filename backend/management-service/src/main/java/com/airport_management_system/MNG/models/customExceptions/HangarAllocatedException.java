package com.airport_management_system.MNG.models.customExceptions;

public class HangarAllocatedException extends RuntimeException {
    public HangarAllocatedException(String message) {
        super(message);
    }
}
