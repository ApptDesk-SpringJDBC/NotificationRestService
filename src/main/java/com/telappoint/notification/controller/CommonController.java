package com.telappoint.notification.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;

import com.telappoint.notification.common.model.ResponseModel;
import com.telappoint.notification.handlers.exception.AppsExceptionHandler;

/**
 * 
 * @author Balaji N
 *
 */
@Controller
public class CommonController extends AppsExceptionHandler {
	
	public ResponseModel populateJDHSuccessData(Logger logger, Object data) {
		return populateJDHSuccessData(logger, data, true);
	}

	public ResponseModel populateJDHData(Logger logger, Object data) {
		return populateJDHSuccessData(logger, data, true);
	}

	public ResponseModel populateJDHSuccessData(Logger logger, Object data, boolean logging) {
		if (logging == false) {
			//TODO: log it
		}

		ResponseModel responseModel = new ResponseModel();
		responseModel.setData(data);
		return responseModel;
	}
}
