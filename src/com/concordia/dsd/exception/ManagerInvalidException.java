package com.concordia.dsd.exception;

public class ManagerInvalidException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1104771802525147788L;

	private String message;

	public ManagerInvalidException(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
