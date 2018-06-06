package com.telappoint.notification.common.model;

public class ErrorLog extends BaseRequest {
	private String errorCode;
	private String internalErrorMessage;
	private String userErrorMessage;
	
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getInternalErrorMessage() {
		return internalErrorMessage;
	}
	public void setInternalErrorMessage(String internalErrorMessage) {
		this.internalErrorMessage = internalErrorMessage;
	}
	public String getUserErrorMessage() {
		return userErrorMessage;
	}
	public void setUserErrorMessage(String userErrorMessage) {
		this.userErrorMessage = userErrorMessage;
	}
}
