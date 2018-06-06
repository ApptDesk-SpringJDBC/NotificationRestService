package com.telappoint.notification.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * 
 * @author Balaji
 * 
 */
@JsonSerialize(include = Inclusion.NON_NULL)
public class CampaignMessageSMS {
	private Long campaignMessageSMSId;
	private Long campaignId;
	private String lang;
	private String subject;
	private String message;

	public Long getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(Long campaignId) {
		this.campaignId = campaignId;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public Long getCampaignMessageSMSId() {
		return campaignMessageSMSId;
	}

	public void setCampaignMessageSMSId(Long campaignMessageSMSId) {
		this.campaignMessageSMSId = campaignMessageSMSId;
	}
}
