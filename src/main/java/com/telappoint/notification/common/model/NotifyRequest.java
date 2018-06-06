package com.telappoint.notification.common.model;

import java.util.Date;

/**
 * 
 * @author Balaji N
 *
 */
public class NotifyRequest extends BaseRequest {
	// admin
	private Long notifyReminderStatusId;
	private Long notifyId;
	private String notes;

	// below three fields will be populated
	// based on reminder_status table.
	private Integer notifyStatus;
	private Integer notifyPhoneStatus;
	private Integer notifySMSStatus;
	private Integer notifyEmailStatus;

	private String notifyByEmail;
	private String notifyByPhone;
	private String notifyBySMS;

	// ivr
	private Integer attemptId;
	private String phoneNumber;
	private Date startTime;
	private long leadTime;
	private long lagTime;
	private String action;

	public Integer getAttemptId() {
		return attemptId;
	}

	public void setAttemptId(Integer attemptId) {
		this.attemptId = attemptId;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public long getLeadTime() {
		return leadTime;
	}

	public void setLeadTime(long leadTime) {
		this.leadTime = leadTime;
	}

	public long getLagTime() {
		return lagTime;
	}

	public void setLagTime(long lagTime) {
		this.lagTime = lagTime;
	}

	public Integer getNotifyStatus() {
		return notifyStatus;
	}

	public void setNotifyStatus(Integer notifyStatus) {
		this.notifyStatus = notifyStatus;
	}

	public Integer getNotifySMSStatus() {
		return notifySMSStatus;
	}

	public void setNotifySMSStatus(Integer notifySMSStatus) {
		this.notifySMSStatus = notifySMSStatus;
	}

	public Integer getNotifyEmailStatus() {
		return notifyEmailStatus;
	}

	public void setNotifyEmailStatus(Integer notifyEmailStatus) {
		this.notifyEmailStatus = notifyEmailStatus;
	}

	public Long getNotifyId() {
		return notifyId;
	}

	public void setNotifyId(Long notifyId) {
		this.notifyId = notifyId;
	}

	public Integer getNotifyPhoneStatus() {
		return notifyPhoneStatus;
	}

	public void setNotifyPhoneStatus(Integer notifyPhoneStatus) {
		this.notifyPhoneStatus = notifyPhoneStatus;
	}

	public Long getNotifyReminderStatusId() {
		return notifyReminderStatusId;
	}

	public void setNotifyReminderStatusId(Long notifyReminderStatusId) {
		this.notifyReminderStatusId = notifyReminderStatusId;
	}

	public String getNotifyByEmail() {
		return notifyByEmail;
	}

	public void setNotifyByEmail(String notifyByEmail) {
		this.notifyByEmail = notifyByEmail;
	}

	public String getNotifyByPhone() {
		return notifyByPhone;
	}

	public void setNotifyByPhone(String notifyByPhone) {
		this.notifyByPhone = notifyByPhone;
	}

	public String getNotifyBySMS() {
		return notifyBySMS;
	}

	public void setNotifyBySMS(String notifyBySMS) {
		this.notifyBySMS = notifyBySMS;
	}
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
