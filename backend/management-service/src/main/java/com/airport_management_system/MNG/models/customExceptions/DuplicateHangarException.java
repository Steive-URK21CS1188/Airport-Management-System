package com.airport_management_system.MNG.models.customExceptions;

public class DuplicateHangarException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DuplicateHangarException(String message) {
        super(message);
    }
}
