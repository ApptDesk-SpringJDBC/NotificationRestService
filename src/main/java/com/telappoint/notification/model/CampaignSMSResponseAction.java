package com.telappoint.notification.model;

public class CampaignSMSResponseAction {
	private Long campaginId;
	private String inputValue;
	private String action;
	public Long getCampaginId() {
		return campaginId;
	}
	public void setCampaginId(Long campaginId) {
		this.campaginId = campaginId;
	}
	public String getInputValue() {
		return inputValue;
	}
	public void setInputValue(String inputValue) {
		this.inputValue = inputValue;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
}
