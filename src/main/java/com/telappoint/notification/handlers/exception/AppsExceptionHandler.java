package com.telappoint.notification.handlers.exception;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.telappoint.notification.common.model.ResponseModel;

/**
 * 
 * @author Balaji N
 *
 */
public class AppsExceptionHandler {

	@ExceptionHandler(Exception.class)
	public @ResponseBody ResponseModel handleException(Logger logger, Exception e) {
		// TODO: Trigger the email to admin and log the exception.
		
		return ResponseModel.exceptionResponse(e);
	}
}