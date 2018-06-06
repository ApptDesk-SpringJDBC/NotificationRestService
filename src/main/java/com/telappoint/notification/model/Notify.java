package com.telappoint.notification.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * 
 * @author Balaji
 * 
 */
@JsonSerialize(include = Inclusion.NON_NULL)
public class Notify {
	private Long notifyId;
	private Long campaignId;
	private String dueDateTime;
	private Integer notifyPhoneStatus;
	private Integer notifyEmailStatus;
	private Integer notifySMSstatus;
	private Integer notifyStatus;
	private Long eventId;
	private String firstName;
	private String lastName;
	private String homePhone;
	private String workPhone;
	private String cellPhone;
	private String email;
	
	private Integer locationId;
	private Integer resourceId;
	private Integer serviceId;
	private String includeAudio1;
	private String includeAudio4;

	private Long seatId;
	private Long eventDateTimeId;
	private Long customerId;
	private Long scheduleId;
	private String notifyByPhone;
	private String notifyBySMS;
	private String notifyByEmail;
	private String notifyByPhoneConfirm;
	private String notifyBySMSConfirm;
	private String notifyByEmailConfirm;
	private String notifyByPushNotif;
	private Integer notifyPushNotificationStatus;
	
	
	public Long getNotifyId() {
		return notifyId;
	}

	public void setNotifyId(Long notifyId) {
		this.notifyId = notifyId;
	}

	public String getDueDateTime() {
		return dueDateTime;
	}

	public void setDueDateTime(String dueDateTime) {
		this.dueDateTime = dueDateTime;
	}

	public Integer getNotifyPhoneStatus() {
		return notifyPhoneStatus;
	}

	public void setNotifyPhoneStatus(Integer notifyPhoneStatus) {
		this.notifyPhoneStatus = notifyPhoneStatus;
	}

	public Integer getNotifyEmailStatus() {
		return notifyEmailStatus;
	}

	public void setNotifyEmailStatus(Integer notifyEmailStatus) {
		this.notifyEmailStatus = notifyEmailStatus;
	}

	public Integer getNotifySMSstatus() {
		return notifySMSstatus;
	}

	public void setNotifySMSstatus(Integer notifySMSstatus) {
		this.notifySMSstatus = notifySMSstatus;
	}

	public Integer getNotifyStatus() {
		return notifyStatus;
	}

	public void setNotifyStatus(Integer notifyStatus) {
		this.notifyStatus = notifyStatus;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getHomePhone() {
		return homePhone;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	public String getWorkPhone() {
		return workPhone;
	}

	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

	public String getCellPhone() {
		return cellPhone;
	}

	public void setCellPhonel(String cellPhone) {
		this.cellPhone = cellPhone;
	}

	public Long getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(Long campaignId) {
		this.campaignId = campaignId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	public Long getSeatId() {
		return seatId;
	}

	public void setSeatId(Long seatId) {
		this.seatId = seatId;
	}

	public Long getEventDateTimeId() {
		return eventDateTimeId;
	}

	public void setEventDateTimeId(Long eventDateTimeId) {
		this.eventDateTimeId = eventDateTimeId;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public Long getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(Long scheduleId) {
		this.scheduleId = scheduleId;
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

	public String getNotifyByEmail() {
		return notifyByEmail;
	}

	public void setNotifyByEmail(String notifyByEmail) {
		this.notifyByEmail = notifyByEmail;
	}

	public String getNotifyByPhoneConfirm() {
		return notifyByPhoneConfirm;
	}

	public void setNotifyByPhoneConfirm(String notifyByPhoneConfirm) {
		this.notifyByPhoneConfirm = notifyByPhoneConfirm;
	}

	public String getNotifyBySMSConfirm() {
		return notifyBySMSConfirm;
	}

	public void setNotifyBySMSConfirm(String notifyBySMSConfirm) {
		this.notifyBySMSConfirm = notifyBySMSConfirm;
	}

	public String getNotifyByEmailConfirm() {
		return notifyByEmailConfirm;
	}

	public void setNotifyByEmailConfirm(String notifyByEmailConfirm) {
		this.notifyByEmailConfirm = notifyByEmailConfirm;
	}

	public String getNotifyByPushNotif() {
		return notifyByPushNotif;
	}

	public void setNotifyByPushNotif(String notifyByPushNotif) {
		this.notifyByPushNotif = notifyByPushNotif;
	}

	public Integer getNotifyPushNotificationStatus() {
		return notifyPushNotificationStatus;
	}

	public void setNotifyPushNotificationStatus(Integer notifyPushNotificationStatus) {
		this.notifyPushNotificationStatus = notifyPushNotificationStatus;
	}

	public void setCellPhone(String cellPhone) {
		this.cellPhone = cellPhone;
	}
	
	public Integer getResourceId() {
		return resourceId;
	}

	public void setResourceId(Integer resourceId) {
		this.resourceId = resourceId;
	}

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}
	
	public String getIncludeAudio1() {
		return includeAudio1;
	}

	public void setIncludeAudio1(String includeAudio1) {
		this.includeAudio1 = includeAudio1;
	}

	public String getIncludeAudio4() {
		return includeAudio4;
	}

	public void setIncludeAudio4(String includeAudio4) {
		this.includeAudio4 = includeAudio4;
	}
}
