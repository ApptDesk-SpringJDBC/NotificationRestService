package com.telappoint.notification.model;

/**
 * 
 * @author Balaji N
 *
 */
public class NotifyPhoneStatus {
	private Long notifyPhoneStatusId;
	private Integer attemptId;
	private Integer callStatus;
	private String callTimestamp;
	private String phone;
	private Integer seconds;
	private Long notifyId;

	public Long getNotifyPhoneStatusId() {
		return notifyPhoneStatusId;
	}

	public void setNotifyPhoneStatusId(Long notifyPhoneStatusId) {
		this.notifyPhoneStatusId = notifyPhoneStatusId;
	}

	public Integer getAttemptId() {
		return attemptId;
	}

	public void setAttemptId(Integer attemptId) {
		this.attemptId = attemptId;
	}

	public Integer getCallStatus() {
		return callStatus;
	}

	public void setCallStatus(Integer callStatus) {
		this.callStatus = callStatus;
	}

	public String getCallTimestamp() {
		return callTimestamp;
	}

	public void setCallTimestamp(String callTimestamp) {
		this.callTimestamp = callTimestamp;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Integer getSeconds() {
		return seconds;
	}

	public void setSeconds(Integer seconds) {
		this.seconds = seconds;
	}

	public Long getNotifyId() {
		return notifyId;
	}

	public void setNotifyId(Long notifyId) {
		this.notifyId = notifyId;
	}
}
