package com.telappoint.notification.handlers.exception;

import org.springframework.http.HttpStatus;

/**
 * 
 * @author Balaji N
 *
 */
public class TelAppointException extends Exception {
	public String errorCode;
	public String internalErrorMessage;
	public String userErrorMessage;
	public HttpStatus httpStatus;
	public Object inputData;
	
	public TelAppointException(String errorCode, String userMessage,  HttpStatus httpStatus,String internalErrorMessage, Object inputData) {
		setErrorCode(errorCode);
		setUserErrorMessage(userMessage);
		setInternalErrorMessage(internalErrorMessage);
		setHttpStatus(httpStatus);
		setInputData(inputData);
	}

	public TelAppointException() {

	}

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

	public Object getInputData() {
		return inputData;
	}

	public void setInputData(Object inputData) {
		this.inputData = inputData;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}

	@Override
	public String toString() {
		return "BuildException [errorCode=" + errorCode + ", internalErrorMessage=" + internalErrorMessage + ", userErrorMessage=" + userErrorMessage + ", httpStatus="
				+ httpStatus + ", inputData=" + inputData + "]";
	}
	
}
