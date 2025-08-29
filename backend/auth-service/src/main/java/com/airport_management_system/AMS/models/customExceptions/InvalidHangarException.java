package com.airport_management_system.AMS.models.customExceptions;

public class InvalidHangarException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidHangarException(String message) {
        super(message);
    }
}
