package com.telappoint.notification.common.constants;

/**
 * 
 * @author Balaji N
 *
 */
public enum PropertiesConstants {

	NOTIFY_SERVICE_REST_WS_PROP("notificationRestService.properties"),
	PHONE_LOG_FILE_UPLOAD_PROP("phoneLogFileUpload.properties");
	
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
