package com.telappoint.notification.common.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * 
 * @author Balaji N
 *
 */
@JsonSerialize(include = Inclusion.NON_NULL)
public class Campaign {
	private long campaignId;
	private String campaignName;
	private String notifyByPhone;
	private String notifyByPhoneConfirm;
	private String notifyBySMS;
	private String notifyBySMSConfirm;
	private String notifyByEmail;
	private String notifyByEmailConfirm;
	private String notifyByPushNotification;
	private String active;

	public long getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(long campaignId) {
		this.campaignId = campaignId;
	}

	public String getCampaignName() {
		return campaignName;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	public String getNotifyByPhone() {
		return notifyByPhone;
	}

	public void setNotifyByPhone(String notifyByPhone) {
		this.notifyByPhone = notifyByPhone;
	}

	public String getNotifyByPhoneConfirm() {
		return notifyByPhoneConfirm;
	}

	public void setNotifyByPhoneConfirm(String notifyByPhoneConfirm) {
		this.notifyByPhoneConfirm = notifyByPhoneConfirm;
	}

	public String getNotifyBySMS() {
		return notifyBySMS;
	}

	public void setNotifyBySMS(String notifyBySMS) {
		this.notifyBySMS = notifyBySMS;
	}

	public String getNotifyBySMSConfirm() {
		return notifyBySMSConfirm;
	}

	public void setNotifyBySMSConfirm(String notifyBySMSConfirm) {
		this.notifyBySMSConfirm = notifyBySMSConfirm;
	}

	public String getNotifyByEmail() {
		return notifyByEmail;
	}

	public void setNotifyByEmail(String notifyByEmail) {
		this.notifyByEmail = notifyByEmail;
	}

	public String getNotifyByEmailConfirm() {
		return notifyByEmailConfirm;
	}

	public void setNotifyByEmailConfirm(String notifyByEmailConfirm) {
		this.notifyByEmailConfirm = notifyByEmailConfirm;
	}

	public String getNotifyByPushNotification() {
		return notifyByPushNotification;
	}

	public void setNotifyByPushNotification(String notifyByPushNotification) {
		this.notifyByPushNotification = notifyByPushNotification;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

}
