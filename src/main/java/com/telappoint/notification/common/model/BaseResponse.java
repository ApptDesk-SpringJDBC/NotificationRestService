package com.telappoint.notification.common.model;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * 
 * @author Balaji N
 *
 */
@JsonAutoDetect
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class BaseResponse {
	private boolean responseStatus = true;
	private String responseMessage;

	private boolean errorStatus = false;
	private String errorCode;
	private String internalErrorMessage;
	private String userErrorMessage;

	public String getInternalErrorMessage() {
		return internalErrorMessage;
	}

	public void setInternalErrorMessage(String internalErrorMessage) {
		this.internalErrorMessage = internalErrorMessage;
	}

	public boolean isResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(boolean responseStatus) {
		this.responseStatus = responseStatus;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public boolean isErrorStatus() {
		return errorStatus;
	}

	public void setErrorStatus(boolean errorStatus) {
		this.errorStatus = errorStatus;
	}


	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getUserErrorMessage() {
		return userErrorMessage;
	}

	public void setUserErrorMessage(String userErrorMessage) {
		this.userErrorMessage = userErrorMessage;
	}
}
