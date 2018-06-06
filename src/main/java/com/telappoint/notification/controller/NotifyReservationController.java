package com.telappoint.notification.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.telappoint.notification.common.model.ResponseModel;
import com.telappoint.notification.handlers.exception.TelAppointException;
import com.telappoint.notification.model.NotificationResponse;
import com.telappoint.notification.service.NotifyReservationService;

/**
 * 
 * @author Balaji N
 *
 */
@Controller
@RequestMapping("/notifications")
public class NotifyReservationController extends CommonController {

	@Autowired
	private NotifyReservationService notifyReservationService;

	@RequestMapping(method = RequestMethod.GET, value = "/lockDialer", produces = "application/json")
	public @ResponseBody ResponseEntity<ResponseModel> lockDialer(HttpServletRequest request, @RequestParam("clientCode") String clientCode, @RequestParam("device") String device) {
		Logger logger = null;
		try {
			logger = notifyReservationService.getLogger(clientCode, "INFO");
			String ipAddress = request.getRemoteAddr();
			if (checkIP(ipAddress)) {
				sendEmailIPNotAllowed(clientCode, logger, ipAddress, Thread.currentThread().getStackTrace()[1].getMethodName());
			}
			return notifyReservationService.lockDialer(clientCode,logger, device);
		} catch (TelAppointException e) {
			return notifyReservationService.handleException(logger, e);
		}
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/unLockDialer", produces = "application/json")
	public @ResponseBody ResponseEntity<ResponseModel> unLockDialer(HttpServletRequest request, @RequestParam("clientCode") String clientCode, @RequestParam("device") String device) {
		Logger logger = null;
		try {
			logger = notifyReservationService.getLogger(clientCode, device);
			String ipAddress = request.getRemoteAddr();
			if (checkIP(ipAddress)) {
				sendEmailIPNotAllowed(clientCode, logger, ipAddress, Thread.currentThread().getStackTrace()[1].getMethodName());
			}
			return notifyReservationService.unLockDialer(clientCode,logger, device);
			
		} catch (TelAppointException tae) {
			return notifyReservationService.handleException(logger, tae);
		}
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/getNotifyList", produces = "application/json")
	public @ResponseBody
	ResponseEntity<ResponseModel> getNotifyList(HttpServletRequest request,@RequestParam("clientCode") String clientCode,
			@RequestParam("device") String device) {
		Logger logger = null;
		try {
			logger = notifyReservationService.getLogger(clientCode, "INFO");
			String ipAddress = request.getRemoteAddr();
			if (checkIP(ipAddress)) {
				sendEmailIPNotAllowed(clientCode, logger, ipAddress, Thread.currentThread().getStackTrace()[1].getMethodName());
			}	
			NotificationResponse notificationResponse = notifyReservationService.getNotifyList(clientCode, logger,"us-en", device);
			if(notificationResponse.isResponseStatus()) {
				return new ResponseEntity<ResponseModel>(notifyReservationService.populateRMDSuccessData(logger, notificationResponse), HttpStatus.ACCEPTED);
			} else {
				return new ResponseEntity<ResponseModel>(notifyReservationService.populateRMDSuccessData(logger, notificationResponse), HttpStatus.CONFLICT);
			}
		} catch (TelAppointException tae) {
			return notifyReservationService.handleException(logger, tae);
		}
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/getNotifyPhoneList", produces = "application/octet-stream")
	public void getNotifyPhoneList(@RequestParam("clientCode") String clientCode,HttpServletResponse response) {
    	ByteArrayOutputStream outputStream =  null;
    	OutputStream out = null;
    	Logger logger = null;
		try {
			logger = notifyReservationService.getLogger(clientCode, "INFO");
			NotificationResponse notificationResponse = notifyReservationService.getNotifyList(clientCode,logger,"us-en", "phone");
			outputStream = notificationResponse.getOutputStream();
			
			if(outputStream == null || "".equals(outputStream)) {
				logger.debug("GetNotifyPhoneList is empty!!!");
			}
			System.out.println("Data:"+outputStream);
			response.setHeader("Content-Disposition","attachment; filename=\"" + clientCode+".csv" + "\"");
			out = response.getOutputStream();			
			response.setContentType("application/text; charset=utf-8");
			out.write(outputStream.toByteArray());  
			out.flush();			
		} catch (Exception e) {
			logger.error("Exception in getNotifyPhoneList - "+e.getMessage(),e);
		}finally{
			try {
				if(out!=null){
					out.close();
				}
				if(outputStream!=null){
					outputStream.close();
				}
			} catch (IOException e) {
				logger.error("Exception in getNotifyPhoneList while closing outputstreams - "+e.getMessage(),e);
			}
		}
	}
	
	

	private void sendEmailIPNotAllowed(String clientCode, Logger logger, String ipaddress, String methodName) throws TelAppointException {
		StringBuilder body = new StringBuilder("IP Not Allowed :" + ipaddress);
		body.append("<br/><br/>");
		body.append("Exception: ");
		body.append("<br/>");
		body.append(methodName);
		//TODO: send email
	}

	private final boolean allowAnyIp = true;
	private final String iptoCheck = "127.0.0.1";

	private boolean checkIP(String ipAddress) {
		if (allowAnyIp) {
			return false;
		}
		return !ipAddress.equals(iptoCheck);
	}

}
