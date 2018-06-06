package com.telappoint.notification.common.constants;

/**
 * This enum is used to get the all types of date and time string formats.
 * 
 * @author Balaji
 */

public enum CommonDateContants {
	EMPTY_STRING(""),
	DATETIME_FORMAT_YYYYMMDDHHMMSS("yyyy-MM-dd hh:mm:ss"),
	DATETIME_FORMAT_YYYYMMDDHHMMSS_TWELWE_HOURS("yyyy-MM-dd hh:mm a"),
	DATETIME_FORMAT_YYYYMMDDHHMMSS_CAP("yyyy-MM-dd HH:mm:ss"),
	DATE_FORMAT_YYYYMMDD("yyyy-MM-dd"),
	TIME_FORMAT_HHMMSS("hh:mm:ss"),
	TIME_FORMAT_HHMMSS_CAP("HH:mm:ss"),
	TIME_FORMAT_TWELVE_HRS("hh:mm a"),
	TIME_FORMAT_HHMMZZZZ("h:mm a zzzz"),
	TIME_FORMAT_HMMA("h:mm a"),
	TIME_FORMAT_TWENTY_FOUR_HRS("HH:mm"),
	DATE_FORMAT_DDMMMYYYY("dd-MMM-yyyy"),
	DATE_FORMAT_DDMMYYYY("dd-MM-yyyy"),
	DATE_FORMAT_DDMMYYYYHHMMSS_CAP("dd-MM-yyyy HH:mm:ss"),
	DATE_FORMAT_MMDDYYYYHHMMSS_CAP("MM/dd/yyyy HH:mm:ss"),
	MM_DD_YY_DATE_FORMAT("MM/dd/yy"),
	//MM_DD_YYYY_DATE_FORMAT("MM-dd-yyyy"),
	MM_DD_YYYY_DATE_FORMAT("MM/dd/yyyy"),
	DAY_OF_THE_WEEK_FORMAT("E"),
	//03/20/2012 - 10:30AM
	DATETIME_FORMAT_MMDDYYYY_HHMM_TWELWE_HOURS("MM/dd/yyyy hh:mm a"),
	FULLTEXTUAL_DAY_FORMAT("EEEEE, MMM d, yyyy"),
	DATETIME_FORMAT_MMDDYYYYHHMM_TWELWE_HOURS("MM-dd-yyyy hh:mm a");
	
	private String value;

	private CommonDateContants(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
