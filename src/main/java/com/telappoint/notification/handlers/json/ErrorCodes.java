package com.telappoint.notification.handlers.json;

/**
 * 
 * @author Balaji Nandarapu
 *
 */
public enum ErrorCodes {
	NO_ERROR("0000", "success"), 
	DB_ERROR("9999","Could not get JDBC Connection or DB down.");
	
	private String errorCode;
	private String description;

	private ErrorCodes(String errorCode, String description) {
		this.errorCode = errorCode;
		this.description = description;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getDescription() {
		return description;
	}
}
