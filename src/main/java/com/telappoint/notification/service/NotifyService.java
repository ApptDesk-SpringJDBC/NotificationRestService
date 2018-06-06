package com.telappoint.notification.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;

import com.telappoint.notification.common.model.BaseResponse;
import com.telappoint.notification.common.model.ResponseModel;
import com.telappoint.notification.handlers.exception.TelAppointException;
import com.telappoint.notification.model.EmailSMSStatusResponse;
import com.telappoint.notification.model.FileUploadResponse;
import com.telappoint.notification.model.NotificationResponse;

/**
 * 
 * @author Balaji N
 *
 */
public interface NotifyService {
	public Logger getLogger(String clientCode, String device) throws TelAppointException;
	public ResponseEntity<ResponseModel> getDialerLockStatus(String clientCode, Logger Logger, String device) throws TelAppointException;
	public ResponseEntity<ResponseModel> lockDialer(String clientCode, Logger Logger, String device) throws TelAppointException;
	public ResponseEntity<ResponseModel> updateDialerLockEndTime(String clientCode, Logger Logger, String device) throws TelAppointException;
	public ResponseEntity<ResponseModel> unLockDialer(String clientCode, Logger Logger, String device) throws TelAppointException;
	public ByteArrayOutputStream getPhoneNotificationList(String clientCode, Logger logger, String langCode, String device) throws TelAppointException;
	public NotificationResponse sendEmailNotifications(String clientCode, Logger logger, String langCode, String device) throws TelAppointException;
	public NotificationResponse sendSMSNotifications(String clientCode, Logger logger, String langCode, String device) throws TelAppointException;
	public ResponseEntity<ResponseModel> handleException(Logger logger, Exception tae);
	public ResponseModel populateRMDSuccessData(Logger logger, Object data) ;
	public Logger changeLogLevel(String clientCode, String logLevel) throws TelAppointException;
	public EmailSMSStatusResponse updateEmailNotifyStatus(String clientCode, Logger logger, String token, int notifyStatus, long notifyId) throws TelAppointException;
	public String processSMSConfirmOrCancel(Logger logger, String replyMessage, String from, String to) throws TelAppointException;
	public FileUploadResponse processThePhoneLogFile(String clientCode, Logger logger, long fileSize, String device, String transId, InputStream inputStream, String fileExtension) throws TelAppointException;
	public BaseResponse updateSMSHistory(String clientCode, Logger logger, String device) throws TelAppointException;
}
