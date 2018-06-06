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
	private String inputResponse1;
	private String inputResponse2;
	private String elseResponse;

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

	public String getInputResponse1() {
		return inputResponse1;
	}

	public void setInputResponse1(String inputResponse1) {
		this.inputResponse1 = inputResponse1;
	}

	public String getInputResponse2() {
		return inputResponse2;
	}

	public void setInputResponse2(String inputResponse2) {
		this.inputResponse2 = inputResponse2;
	}

	public String getElseResponse() {
		return elseResponse;
	}

	public void setElseResponse(String elseResponse) {
		this.elseResponse = elseResponse;
	}
}
