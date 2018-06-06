package com.telappoint.notification.constants;

/**
 * 
 * 
 * @author Balaji
 */

public enum FileUploadFieldContants {
	
	/**
	 *  This constant for Notify,Customer/Phone file upload
	 */
	FIRST_NAME("FIRST_NAME","First name field name"),
	NAME("NAME","First name and last name field name"),
	LAST_NAME("LAST_NAME","Last name field name"),
	HOME_PHONE("HOME_PHONE","Phone number field name"),
	CELL_PHONE("CELL_PHONE","Cell Phone field name"),
	WORK_PHONE("WORK_PHONE","Work phone field name"),
	CONTACT_PHONE("CONTACT_PHONE","Contact phone field name"),
	EMAIL("EMAIL","Email field name"),
	LANG_CODE("LANG_CODE","Lang code field name"),
	
	NOTIFY_BY_PHONE("NOTIFY_BY_PHONE","notify by phone field name"),
	NOTIFY_BY_SMS("NOTIFY_BY_SMS","notify by sms"),
	NOTIFY_BY_EMAIL("NOTIFY_BY_EMAIL","notify by email status"),
	NOTIFY_BY_PUSH("NOTIFY_BY_PUSH","notify by push"),
	NOTIFY_BY_PHONE_CONFIRM("NOTIFY_BY_PHONE_CONFIRM","notify by phone confirm"),
	NOTIFY_BY_SMS_CONFIRM("NOTIFY_BY_SMS_CONFIRM","notify by sms confirm"),
	NOTIFY_BY_EMAIL_CONFIRM("NOTIFY_BY_EMAIL_CONFIRM","notify by email confirm status"),
	
	DUE_DATE_TIME("DUE_DATE_TIME","due date time constant check"),
	DUE_DATE("DUE_DATE","due date constant"),
	DUE_TIME("DUE_TIME","due time constant"),
	RESOURCE_CODE("RESOURCE_CODE","resource code constant"),
	LOCATION_CODE("LOCATION_CODE","location code constant"),
	SERVICE_CODE("SERVICE_CODE","service code constant"),
	CUSTOMER_ID("CUSTOMER_ID","customer id constant"),
	
	// Used for Customer File upload
	FILE_UPLOAD_HISTORY_VIEW_DAYS_FROM("5","5"),
	CUST_ID_ACCT_NUM("CUST_ID_ACCT_NUM","customer id account number"),
	NOTIFICATION_PREFERENCE("NOTIFICATION_PREFERENCE","notification preference"),
	WORKER_COMM_PREF("WORKER_COMM_PREF","worker comm pref"),
	CAMPAIGN_CODE("CAMPAIGN_CODE","campaign code"),
	ON_SITE_DATE("ON_SITE_DATE","on site date"),
	DEMOB_DATE("DEMOB_DATE","demob date"),
	RESOURCE_EMP_ID("RESOURCE_EMP_ID","resource employee id"),
	
	// Used for PhoneLogFileUpload
	PICK_UPS("PICK_UPS","pick ups"),
	PHONE_NUMBER("PHONE_NUMBER","phone number field name"),
	DATE_TIME("DATE_TIME","date time"),
	REASON("REASON","reason"),
	DURATION("DURATION","duration"),
	PARAMETERS("PARAMETERS","parameters"),
	ATTEMPTS("ATTEMPTS","attempts"),
	RESULT("RESULT","results"),
	CAUSE("CAUSE","cause"),
	
	// preference file upload constants
	
	PHONE_PREFERENCES("P","phone"),
	TEXT_PREFERENCES("T","sms"),
	EMAIL_PREFERENCES("E","email"),
	PT_PREFERENCES("PT","phone and text"),
	TE_PREFERENCES("TE","text and email"),
	PE_PREFERENCES("PE","phone and email"),
	PTE_PREFERENCES("PTE","phone, text and email"),

	CAMPAIGN_VARIABL_FOR_I("I","first campaign"),
	CAMPAIGN_VARIABL_FOR_C("C","second campaign"),
	CAMPAIGN_VARIABL_FOR_D("D","third campaign"),
	
	// remove characters
	DAY_EEEEE("DAY_EEEEE","week of the day"),
	REMOVE_AFTER_CHAR("REMOVE_AFTER_CHAR","remove after char"),
	REMOVE_BEFORE_CHAR("REMOVE_BEFORE_CHAR","remove before char"),
	REMOVE_STRING("REMOVE_STRING","remove string"),
	REJECT_IF_CHAR_FOUND("REJECT_IF_CHAR_FOUND","reject if char found"),
	
	// update few fields in notify table based on some assumption
	CHANGE_NOTIFY_ID("CHANGE_NOTIFY_ID","change notify Id"),
	NOTIFY_ID("NOTIFY_ID","change notify Id"),
	NOTIFY_SEND_MESSAGE_TYPE("NOTIFY_SEND_MESSAGE_TYPE","notify send message type"),
	MEMBER_ID("MEMBER_ID","memberId"),
	DOB("DOB","dob");

	private String value;
	private String description;

	private FileUploadFieldContants(String value,String description) {
		this.setValue(value);
		this.setDescription(description);
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
