package com.airport_management_system.MNG.models.customExceptions;

public class UnauthorizedAccessException extends RuntimeException {
	private static final long serialVersionUID = 1L;
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
