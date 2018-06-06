package com.telappoint.notification.common.constants;

/**
 * @author Balaji
 */
public enum NotifyFlowStateConstants {
	GET_DIALER_SETTING("GET_DIALER_SETTING"),
	GET_CAMPAIGN_BY_ID("GET_CAMPAIGN_BY_ID"),
	GET_CAMPAIGNS("GET_CAMPAIGNS"),
	GET_REMINDER_STATUS_LIST("GET_REMINDER_STATUS_LIST"),
	UPDATE_REMINDER_STATUS("UPDATE_REMINDER_STATUS"),
	GET_NOTIFY_LIST("GET_NOTIFY_LIST"),
	NOTIFY_PHONE_STATUS("NOTIFY_PHONE_STATUS");
	
	private String value;

	private NotifyFlowStateConstants(String value) {
		this.setValue(value);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
