package com.airport_management_system.MNG.models.customExceptions;

public class PlaneUnavailableException extends RuntimeException {

	    public PlaneUnavailableException() {
	        super();
	    }

	    public PlaneUnavailableException(String message) {
	        super(message);
	    }

	    public PlaneUnavailableException(String message, Throwable cause) {
	        super(message, cause);
	    }

	    public PlaneUnavailableException(Throwable cause) {
	        super(cause);
	    }
}

