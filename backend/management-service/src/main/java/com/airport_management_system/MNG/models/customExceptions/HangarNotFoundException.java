package com.airport_management_system.MNG.models.customExceptions;

public class HangarNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public HangarNotFoundException(String message) {
        super(message);
    }
}
