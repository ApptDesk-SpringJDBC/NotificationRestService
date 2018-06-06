package com.telappoint.notification.common.constants;

/**
 * 
 * @author Balaji N
 *
 */
public enum ErrorConstants {
	// DB layer error codes
	ERROR_1000("1000", "Error in getClient fetch."), 
	ERROR_1001("1001", "Error in getClientDeploymentConfig fetch."), 
	ERROR_1002("1002", "Error in getSMSConfig fetch."), 
	ERROR_1003("1003", "Error in getDailerSettings fetch."),
	ERROR_1004("1004", "Error in getNotifyList"),
	ERROR_1005("1005", "Error while updateNotifyStatus status."),
	ERROR_1006("1006", "Error while getCampaignMessageEmails fetch."),
	ERROR_1007("1007", "Error while getNotifyWithCustomerIdGreaterThanZero fetch."),
	ERROR_1008("1008", "Error while getCampaignMessagePhone fetch."),
	ERROR_1009("1009", "Error while getPlaceHolderMap fetch."),
	ERROR_1010("1010", "Error while getDynamicPlaceHolder fetch."),
	ERROR_1011("1011", "Error while getPlaceHolderCategories fetch."),
	ERROR_1012("1012", "Error while savePhoneRecords."),
	ERROR_1013("1013", "Error while saveNotifyPhoneStatusRecords."),
	ERROR_1014("1014", "Error while updateNotifyPhoneStatusSeconds."),
	ERROR_1015("1015", "Error while getCampaignMessageSMS."),
	
	
	// Service layer error codes
	ERROR_2000("2000", "Error while preparePhoneNotificationResponse csv file"),
	ERROR_2999("2999", "Error while getLogger instance.");
	
	private String errorCode;
	private String userErrorMessage;

	private ErrorConstants(String errorCode, String userErrorMessage) {
		this.errorCode = errorCode;
		this.userErrorMessage = userErrorMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getUserErrorMessage() {
		return userErrorMessage;
	}

	public void setUserErrorMessage(String userErrorMessage) {
		this.userErrorMessage = userErrorMessage;
	}
}
