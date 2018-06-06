package com.telappoint.notification.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.telappoint.notification.common.model.BaseResponse;

/**
 * 
 * @author Balaji
 * 
 */
@JsonSerialize(include = Inclusion.NON_NULL)
public class NotificationResponse extends BaseResponse {
	public int noOfNotificationsSentSuccess=0;
	public int noOfNotificationsSentFailed=0;
	public int getNoOfNotificationsSentSuccess() {
		return noOfNotificationsSentSuccess;
	}
	public void setNoOfNotificationsSentSuccess(int noOfNotificationsSentSuccess) {
		this.noOfNotificationsSentSuccess = noOfNotificationsSentSuccess;
	}
	public int getNoOfNotificationsSentFailed() {
		return noOfNotificationsSentFailed;
	}
	public void setNoOfNotificationsSentFailed(int noOfNotificationsSentFailed) {
		this.noOfNotificationsSentFailed = noOfNotificationsSentFailed;
	}
}
