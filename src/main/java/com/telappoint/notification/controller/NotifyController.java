package com.telappoint.notification.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.telappoint.notification.common.constants.ErrorConstants;
import com.telappoint.notification.common.constants.PropertiesConstants;
import com.telappoint.notification.common.logger.AsynchLogger;
import com.telappoint.notification.common.logger.LoggerPool;
import com.telappoint.notification.common.model.BaseResponse;
import com.telappoint.notification.common.model.ResponseModel;
import com.telappoint.notification.common.utils.PropertyUtils;
import com.telappoint.notification.handlers.exception.TelAppointException;
import com.telappoint.notification.model.FileUploadResponse;
import com.telappoint.notification.model.NotificationResponse;
import com.telappoint.notification.service.NotifyService;

/**
 * 
 * @author Balaji N
 *
 */
@Controller
@RequestMapping("/notifications")
public class NotifyController extends CommonController {

	@Autowired
	private NotifyService notifyService;

	@RequestMapping(method = RequestMethod.GET, value = "/getDialerLockStatus", produces = "application/json")
	public @ResponseBody ResponseEntity<ResponseModel> getDialerLockStatus(HttpServletRequest request, @RequestParam("clientCode") String clientCode,
			@RequestParam("device") String device) {
		Logger logger = null;
		try {
			logger = notifyService.getLogger(clientCode, "INFO");
			String ipAddress = request.getRemoteAddr();
			if (checkIP(ipAddress)) {
				sendEmailIPNotAllowed(clientCode, logger, ipAddress, Thread.currentThread().getStackTrace()[1].getMethodName());
			}
			return notifyService.getDialerLockStatus(clientCode, logger, device);
		} catch (TelAppointException e) {
			return notifyService.handleException(logger, e);
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/lockDialer", produces = "application/json")
	public @ResponseBody ResponseEntity<ResponseModel> lockDialer(HttpServletRequest request, @RequestParam("clientCode") String clientCode, @RequestParam("device") String device) {
		Logger logger = null;
		try {
			logger = notifyService.getLogger(clientCode, "INFO");
			String ipAddress = request.getRemoteAddr();
			if (checkIP(ipAddress)) {
				sendEmailIPNotAllowed(clientCode, logger, ipAddress, Thread.currentThread().getStackTrace()[1].getMethodName());
			}
			return notifyService.lockDialer(clientCode, logger, device);
		} catch (TelAppointException e) {
			return notifyService.handleException(logger, e);
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/updateDialerLockEndTime", produces = "application/json")
	public @ResponseBody ResponseEntity<ResponseModel> updateDialerLockEndTime(HttpServletRequest request, @RequestParam("clientCode") String clientCode,
			@RequestParam("device") String device) {
		Logger logger = null;
		try {
			logger = notifyService.getLogger(clientCode, "INFO");
			String ipAddress = request.getRemoteAddr();
			if (checkIP(ipAddress)) {
				sendEmailIPNotAllowed(clientCode, logger, ipAddress, Thread.currentThread().getStackTrace()[1].getMethodName());
			}
			return notifyService.updateDialerLockEndTime(clientCode, logger, device);
		} catch (TelAppointException e) {
			return notifyService.handleException(logger, e);
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/unLockDialer", produces = "application/json")
	public @ResponseBody ResponseEntity<ResponseModel> unLockDialer(HttpServletRequest request, @RequestParam("clientCode") String clientCode, @RequestParam("device") String device) {
		Logger logger = null;
		try {
			logger = notifyService.getLogger(clientCode, device);
			String ipAddress = request.getRemoteAddr();
			if (checkIP(ipAddress)) {
				sendEmailIPNotAllowed(clientCode, logger, ipAddress, Thread.currentThread().getStackTrace()[1].getMethodName());
			}
			return notifyService.unLockDialer(clientCode, logger, device);

		} catch (TelAppointException tae) {
			return notifyService.handleException(logger, tae);
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/sendEmailNotifications", produces = "application/json")
	public @ResponseBody ResponseEntity<ResponseModel> sendEmailNotifications(HttpServletRequest request, @RequestParam("clientCode") String clientCode,
			@RequestParam("device") String device) {
		Logger logger = null;
		try {
			logger = notifyService.getLogger(clientCode, "INFO");
			String ipAddress = request.getRemoteAddr();
			if (checkIP(ipAddress)) {
				sendEmailIPNotAllowed(clientCode, logger, ipAddress, Thread.currentThread().getStackTrace()[1].getMethodName());
			}
			NotificationResponse notificationResponse = notifyService.sendEmailNotifications(clientCode, logger, "us-en", device);
			if (notificationResponse.isResponseStatus()) {
				return new ResponseEntity<ResponseModel>(notifyService.populateRMDSuccessData(logger, notificationResponse), HttpStatus.OK);
			} else {
				return new ResponseEntity<ResponseModel>(notifyService.populateRMDSuccessData(logger, notificationResponse), HttpStatus.NO_CONTENT);
			}
		} catch (TelAppointException tae) {
			return notifyService.handleException(logger, tae);
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/sendSMSNotifications", produces = "application/json")
	public @ResponseBody ResponseEntity<ResponseModel> sendSMSNotifications(HttpServletRequest request, @RequestParam("clientCode") String clientCode,
			@RequestParam("device") String device) {
		Logger logger = null;
		try {
			logger = notifyService.getLogger(clientCode, "INFO");
			String ipAddress = request.getRemoteAddr();
			if (checkIP(ipAddress)) {
				sendEmailIPNotAllowed(clientCode, logger, ipAddress, Thread.currentThread().getStackTrace()[1].getMethodName());
			}
			NotificationResponse notificationResponse = notifyService.sendSMSNotifications(clientCode, logger, "us-en", device);
			if (notificationResponse.isResponseStatus()) {
				return new ResponseEntity<ResponseModel>(notifyService.populateRMDSuccessData(logger, notificationResponse), HttpStatus.OK);
			} else {
				return new ResponseEntity<ResponseModel>(notifyService.populateRMDSuccessData(logger, notificationResponse), HttpStatus.NO_CONTENT);
			}
		} catch (TelAppointException tae) {
			return notifyService.handleException(logger, tae);
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/getNotifyPhoneList", produces = "application/octet-stream")
	public void getNotifyPhoneList(@RequestParam("clientCode") String clientCode, HttpServletResponse response) {
		ByteArrayOutputStream outputStream = null;
		OutputStream out = null;
		Logger logger = null;
		try {
			logger = notifyService.getLogger(clientCode, "INFO");
			outputStream = notifyService.getPhoneNotificationList(clientCode, logger, "us-en", "phone");
			if (outputStream == null || "".equals(outputStream)) {
				logger.debug("GetNotifyPhoneList is empty!!!");
			}
			response.setHeader("Content-Disposition", "attachment; filename=\"" + clientCode + ".csv" + "\"");
			out = response.getOutputStream();
			response.setContentType("application/text; charset=utf-8");
			out.write(outputStream.toByteArray());
			out.flush();
		} catch (TelAppointException | IOException tae) {
			logger.error("Exception in getNotifyPhoneList - " + tae.getMessage(), tae);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (IOException e) {
				logger.error("Exception in getNotifyPhoneList while closing outputstreams - " + e.getMessage(), e);
			}
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/smsReplySMSReceiveNullTest", produces = "application/xml")
	public @ResponseBody String smsReplySMSReceiveNullTest(@RequestParam("Body") String replyMessage, @RequestParam("From") String custCellNumber,
			@RequestParam("To") String clientNumber, @RequestParam("SmsSid") String SmsSid) throws TelAppointException {
		return null;
	}

	// Payload]smsReplySMSReceivePayLoad:
	// ToCountry=US&ToState=TN&SmsMessageSid=SMd533425850060b52dac6a04364ab964f&NumMedia=0&ToCity=GOODLETTSVILLE&FromZip=37235&
	// SmsSid=SMd533425850060b52dac6a04364ab964f&FromState=TN&SmsStatus=received&FromCity=NASHVILLE&Body=1&FromCountry=US
	// &To=%2B16156495079&ToZip=37072&NumSegments=1&MessageSid=SMd533425850060b52dac6a04364ab964f&AccountSid=AC4458b0b635416cba4238effa76e78e84
	// &From=%2B16155221151&ApiVersion=2010-04-01
	@RequestMapping(method = RequestMethod.GET, value = "/smsReplySMSReceive", produces = "application/xml")
	public @ResponseBody String smsReplySMSReceive(@RequestParam("Body") String replyMessage, @RequestParam("From") String custCellNumber, @RequestParam("To") String clientNumber,
			@RequestParam("SmsSid") String SmsSid) throws Exception {
		Logger logger = null;

		try {
			logger = getDefaultLog();
			System.out.println("smsReplySMSReceive POST method has entered.");
			System.out.println("Data has recived :" + replyMessage + " from the client cell:" + custCellNumber);
			System.out.println("Vendor Phone:" + clientNumber);

			logger.info("smsReplySMSReceive POST method has entered.");
			logger.info("Data has recived :" + replyMessage + " from the client cell:" + custCellNumber);
			logger.info("Vendor Phone:" + clientNumber);

			/*
			 * SMS Gateway is sending phone number including country code. So,
			 * trimming the country codes from cell numbers.
			 */
			if (clientNumber.contains("+1"))
				clientNumber = clientNumber.replace("+1", "");
			if (custCellNumber.contains("+1"))
				custCellNumber = custCellNumber.replace("+1", "");

			String responseToClient = "";

			if (replyMessage == null)
				replyMessage = "";
			if (custCellNumber == null)
				custCellNumber = "";
			if (clientNumber == null)
				clientNumber = "";

			responseToClient = notifyService.processSMSConfirmOrCancel(logger, replyMessage.trim(), custCellNumber.trim(), clientNumber.trim());
			logger.info("responseToClient:" + responseToClient);
			return responseToClient;
		} catch (TelAppointException | IOException tae) {
			logger.error("Error in smsReplySMSReceive process: " + tae, tae);
			return null;
		}
	}

	// not in use
	@RequestMapping(method = RequestMethod.POST, value = "/smsReplySMSReceivePayLoad", produces = "application/xml")
	public @ResponseBody String smsReplySMSReceivePayLoad(HttpServletRequest request) throws IOException {
		StringBuilder sb = new StringBuilder();
		String body = null;
		while ((body = request.getReader().readLine()) != null) {
			sb.append(body);
		}
		System.out.println("[Payload]smsReplySMSReceivePayLoad :" + sb.toString());
		return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><Response><Sms>Message Success</Sms></Response>";
	}

	@RequestMapping(method = RequestMethod.POST, value = "/uploadPhoneLogFile", consumes = MediaType.MULTIPART_FORM_DATA, produces = "application/json")
	public @ResponseBody ResponseEntity<ResponseModel> uploadPhoneLogFile(@RequestParam("clientCode") String clientCode,@RequestParam("file") MultipartFile file) {
		String originalFileName = file.getOriginalFilename();
		String fileExtension;
		Logger logger = null;
		try {
			fileExtension = originalFileName.substring(originalFileName.indexOf(".") + 1);
			long fileSize = file.getSize();
			logger = notifyService.getLogger(clientCode, "online");
			FileUploadResponse fileUploadResponse = new FileUploadResponse();
			byte[] bytes = file.getBytes();
			InputStream inputStream = new ByteArrayInputStream(bytes);
			fileUploadResponse.setErrorResponse("File Extension " + fileExtension + " not allowed");
			if ("EXE".equalsIgnoreCase(fileExtension) || "SQL".equalsIgnoreCase(fileExtension)) {
				return new ResponseEntity<ResponseModel>(notifyService.populateRMDSuccessData(logger, fileUploadResponse), HttpStatus.NOT_ACCEPTABLE);
			}

			fileUploadResponse = notifyService.processThePhoneLogFile(clientCode, logger, fileSize, "online", "1", inputStream, fileExtension);
			return new ResponseEntity<ResponseModel>(notifyService.populateRMDSuccessData(logger, fileUploadResponse), HttpStatus.CREATED);
		} catch (TelAppointException|IOException tae) {
			return notifyService.handleException(logger, tae);
		}
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/updateSMSHistory", produces = "application/json")
	public @ResponseBody ResponseEntity<ResponseModel> updateSMSHistory(HttpServletRequest request, @RequestParam("clientCode") String clientCode,
			@RequestParam("device") String device) {
		Logger logger = null;
		try {
			logger = notifyService.getLogger(clientCode, "INFO");
			String ipAddress = request.getRemoteAddr();
			if (checkIP(ipAddress)) {
				sendEmailIPNotAllowed(clientCode, logger, ipAddress, Thread.currentThread().getStackTrace()[1].getMethodName());
			}
			BaseResponse baseResponse = notifyService.updateSMSHistory(clientCode, logger, device);
			if (baseResponse.isResponseStatus()) {
				return new ResponseEntity<ResponseModel>(notifyService.populateRMDSuccessData(logger, baseResponse), HttpStatus.OK);
			} else {
				return new ResponseEntity<ResponseModel>(notifyService.populateRMDSuccessData(logger, baseResponse), HttpStatus.CREATED);
			}
		} catch (TelAppointException tae) {
			return notifyService.handleException(logger, tae);
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "changeLogLevel", produces = "application/json")
	public @ResponseBody ResponseEntity<ResponseModel> changeLogLevel(HttpServletRequest request, @RequestParam("clientCode") String clientCode,
			@RequestParam("logLevel") String logLevel) throws Exception {
		Logger logger = null;
		try {
			logger = notifyService.changeLogLevel(clientCode, logLevel);
			return new ResponseEntity<ResponseModel>(notifyService.populateRMDSuccessData(logger, new BaseResponse()), HttpStatus.OK);
		} catch (TelAppointException tae) {
			return notifyService.handleException(logger, tae);
		}
	}

	private void sendEmailIPNotAllowed(String clientCode, Logger logger, String ipaddress, String methodName) throws TelAppointException {
		StringBuilder body = new StringBuilder("IP Not Allowed :" + ipaddress);
		body.append("<br/><br/>");
		body.append("Exception: ");
		body.append("<br/>");
		body.append(methodName);
		// TODO: send email
	}

	private Logger getDefaultLog() throws Exception {
		Logger asynchLogger = null;
		try {
			String logFileLocation = PropertyUtils.getValueFromProperties("LOG_LOCATION_PATH", PropertiesConstants.NOTIFY_SERVICE_REST_WS_PROP.getPropertyFileName());
			if (com.telappoint.notification.common.logger.LoggerPool.getHandleToLogger("defaultNotifyServiceLog") == null) {
				asynchLogger = (new AsynchLogger(logFileLocation + "/defaultNotifyServiceLog.log", Level.INFO)).getLogger("defaultNotifyServiceLog");
				LoggerPool.addLogger("defaultNotifyServiceLog", asynchLogger);
			}
			return LoggerPool.getHandleToLogger("defaultNotifyServiceLog");
		} catch (IOException ioe) {
			throw new TelAppointException(ErrorConstants.ERROR_2999.getErrorCode(), ErrorConstants.ERROR_2999.getUserErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR, ioe.getMessage(), null);
		}
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
