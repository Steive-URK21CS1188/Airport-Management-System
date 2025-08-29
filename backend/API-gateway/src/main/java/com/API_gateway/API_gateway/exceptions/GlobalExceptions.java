package com.API_gateway.API_gateway.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptions {
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<String> runtimeexception()
	{
		return new ResponseEntity<String>("No access",HttpStatus.BAD_REQUEST);
	}
}
