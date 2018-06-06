package com.telappoint.notification.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * 
 * @author Balaji
 * 
 */
@JsonSerialize(include = Inclusion.NON_NULL)
public class CampaignMessagePhone {
	private Long campaignMessagePhoneId;
	private Long campaignId;
	private String lang;
	private String ttsOnly;
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTtsOnly() {
		return ttsOnly;
	}

	public void setTtsOnly(String ttsOnly) {
		this.ttsOnly = ttsOnly;
	}

	public Long getCampaignMessagePhoneId() {
		return campaignMessagePhoneId;
	}

	public void setCampaignMessagePhoneId(Long campaignMessagePhoneId) {
		this.campaignMessagePhoneId = campaignMessagePhoneId;
	}
}
