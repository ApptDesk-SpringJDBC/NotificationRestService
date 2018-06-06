package com.telappoint.notification.common.model;

/**
 * 
 * @author Balaji
 *
 */
public class ClientDeploymentConfig {
	private String timeZone;
	private String dateFormat;
	private String dateyyyyFormat;
	private String fullDateFormat;
	private String fullDatetimeFormat;
	private String popupCalendardateFormat;
	private String timeFormat;
	private String phoneFormat;
	private String fullTextualdayFormat;
	private Integer leadTimeInSeconds;
	private Integer lagTimeInSeconds;
	
	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getDateyyyyFormat() {
		return dateyyyyFormat;
	}

	public void setDateyyyyFormat(String dateyyyyFormat) {
		this.dateyyyyFormat = dateyyyyFormat;
	}

	public String getFullDateFormat() {
		return fullDateFormat;
	}

	public void setFullDateFormat(String fullDateFormat) {
		this.fullDateFormat = fullDateFormat;
	}

	public String getFullDatetimeFormat() {
		return fullDatetimeFormat;
	}

	public void setFullDatetimeFormat(String fullDatetimeFormat) {
		this.fullDatetimeFormat = fullDatetimeFormat;
	}

	public String getPopupCalendardateFormat() {
		return popupCalendardateFormat;
	}

	public void setPopupCalendardateFormat(String popupCalendardateFormat) {
		this.popupCalendardateFormat = popupCalendardateFormat;
	}

	public String getTimeFormat() {
		return timeFormat;
	}

	public void setTimeFormat(String timeFormat) {
		this.timeFormat = timeFormat;
	}

	public String getPhoneFormat() {
		return phoneFormat;
	}

	public void setPhoneFormat(String phoneFormat) {
		this.phoneFormat = phoneFormat;
	}

	public String getFullTextualdayFormat() {
		return fullTextualdayFormat;
	}

	public void setFullTextualdayFormat(String fullTextualdayFormat) {
		this.fullTextualdayFormat = fullTextualdayFormat;
	}

	public Integer getLeadTimeInSeconds() {
		return leadTimeInSeconds;
	}

	public void setLeadTimeInSeconds(Integer leadTimeInSeconds) {
		this.leadTimeInSeconds = leadTimeInSeconds;
	}

	public Integer getLagTimeInSeconds() {
		return lagTimeInSeconds;
	}

	public void setLagTimeInSeconds(Integer lagTimeInSeconds) {
		this.lagTimeInSeconds = lagTimeInSeconds;
	}
}
