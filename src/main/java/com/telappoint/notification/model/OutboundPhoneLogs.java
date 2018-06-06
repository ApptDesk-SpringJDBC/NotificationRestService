package com.telappoint.notification.model;

/**
 * 
 * @author Balaji N
 *
 */
public class OutboundPhoneLogs {
	private Long notifyPhoneLogId;
	private Long notifyId;
	private String timeStamp;
	private long callId;
	private int attemptId;
	private String phone;
	private int pickups;
	private String callDate;
	private String reason;
	private int duration;
	private String result;
	private String cause;

	public Long getNotifyPhoneLogId() {
		return notifyPhoneLogId;
	}

	public void setNotifyPhoneLogId(Long notifyPhoneLogId) {
		this.notifyPhoneLogId = notifyPhoneLogId;
	}

	public Long getNotifyId() {
		return notifyId;
	}

	public void setNotifyId(Long notifyId) {
		this.notifyId = notifyId;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public long getCallId() {
		return callId;
	}

	public void setCallId(long callId) {
		this.callId = callId;
	}

	public int getAttemptId() {
		return attemptId;
	}

	public void setAttemptId(int attemptId) {
		this.attemptId = attemptId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getPickups() {
		return pickups;
	}

	public void setPickups(int pickups) {
		this.pickups = pickups;
	}

	public String getCallDate() {
		return callDate;
	}

	public void setCallDate(String callDate) {
		this.callDate = callDate;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}
}
