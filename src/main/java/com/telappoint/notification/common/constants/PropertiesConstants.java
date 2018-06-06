package com.telappoint.notification.common.constants;

/**
 * 
 * @author Balaji N
 *
 */
public enum PropertiesConstants {

	NOTIFY_PHONE_REST_WS_PROP("notifications.properties");
	
	private String propertyFileName;
	
	private PropertiesConstants(String propertyFileName) {
		this.setPropertyFileName(propertyFileName);
	}

	public String getPropertyFileName() {
		return propertyFileName;
	}

	public void setPropertyFileName(String propertyFileName) {
		this.propertyFileName = propertyFileName;
	}
}
