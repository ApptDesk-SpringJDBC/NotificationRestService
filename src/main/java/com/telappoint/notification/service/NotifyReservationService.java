package com.telappoint.notification.service;

import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;

import com.telappoint.notification.common.model.ResponseModel;
import com.telappoint.notification.handlers.exception.TelAppointException;
import com.telappoint.notification.model.NotificationResponse;

/**
 * 
 * @author Balaji N
 *
 */
public interface NotifyReservationService {
	public Logger getLogger(String clientCode, String device) throws TelAppointException;
	public ResponseEntity<ResponseModel> lockDialer(String clientCode, Logger Logger, String device) throws TelAppointException;
	public ResponseEntity<ResponseModel> unLockDialer(String clientCode, Logger Logger, String device) throws TelAppointException;
	public NotificationResponse getNotifyList(String clientCode, Logger Logger, String langCode, String device) throws TelAppointException;
	public ResponseEntity<ResponseModel> handleException(Logger logger, TelAppointException tae);
	public ResponseModel populateRMDSuccessData(Logger logger, Object data) ;
}
