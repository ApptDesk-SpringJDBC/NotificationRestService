package com.telappoint.notification.common.model;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.http.ResponseEntity;

import com.telappoint.notification.handlers.exception.TelAppointException;

/**
 * 
 * @author Balaji N
 *
 */
@JsonAutoDetect
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ResponseModel {
	private Object data;

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public static ResponseEntity<ResponseModel> exceptionResponseNew(Logger logger, TelAppointException be) {
		ResponseModel jsonData = new ResponseModel();
		BaseResponse baseResponse = new BaseResponse();
		baseResponse.setErrorStatus(true);
		baseResponse.setResponseStatus(false);
		baseResponse.setErrorCode(be.getErrorCode());
		baseResponse.setUserErrorMessage(be.getUserErrorMessage());
		jsonData.setData(baseResponse);
		// email
		// audito log
		logger.error("Error Response: "+be.toString());
		logger.error("Error: "+be, be);
		
		return new ResponseEntity<ResponseModel>(jsonData, be.getHttpStatus());
	}
	
	
	public static ResponseModel exceptionResponse(Exception e) {
		ResponseModel jsonData = new ResponseModel();
		BaseResponse baseResponse = new BaseResponse();
		baseResponse.setErrorStatus(true);
		baseResponse.setResponseStatus(false);
		baseResponse.setInternalErrorMessage(e.getMessage());
		// TODO: handle if any specific Exception and log it.
		jsonData.setData(baseResponse);
		
		//email
		// audito log
		return jsonData;
	}
}
