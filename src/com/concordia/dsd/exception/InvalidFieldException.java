package com.concordia.dsd.exception;

public class InvalidFieldException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 10747948395960323L;
	
	private String message;
	
	public InvalidFieldException(String message) {
		this.message = message;
	}
	
	public String getMessage(){
        return message;
    }

}
