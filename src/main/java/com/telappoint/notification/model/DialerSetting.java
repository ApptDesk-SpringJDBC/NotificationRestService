package com.telappoint.notification.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * 
 * @author Balaji
 * 
 */
@JsonSerialize(include = Inclusion.NON_NULL)
public class DialerSetting {
	private Long campaignId;
	private String callerName;
	private String callSun;
	private String callMon;
	private String callTue;
	private String callWed;
	private String callThu;
	private String callFri;
	private String callSat;
	private String callFrom1;
	private String callTo1;
	private String callFrom2;
	private String callTo2;
	private Integer hoursStopCalling;
	private Integer daysBeforeStartCalling=0;
	private Integer totMaxAttempts;
	private Integer maxAttemptsPerDay;
	private Integer timeIntervalBetweenAttempts;
	private Integer busDaysBeforeStartCalling=0;
	
	public String getCallerName() {
		return callerName;
	}

	public void setCallerName(String callerName) {
		this.callerName = callerName;
	}

	public String getCallSun() {
		return callSun;
	}

	public void setCallSun(String callSun) {
		this.callSun = callSun;
	}

	public String getCallMon() {
		return callMon;
	}

	public void setCallMon(String callMon) {
		this.callMon = callMon;
	}

	public String getCallTue() {
		return callTue;
	}

	public void setCallTue(String callTue) {
		this.callTue = callTue;
	}

	public String getCallWed() {
		return callWed;
	}

	public void setCallWed(String callWed) {
		this.callWed = callWed;
	}

	public String getCallThu() {
		return callThu;
	}

	public void setCallThu(String callThu) {
		this.callThu = callThu;
	}

	public String getCallFri() {
		return callFri;
	}

	public void setCallFri(String callFri) {
		this.callFri = callFri;
	}

	public String getCallSat() {
		return callSat;
	}

	public void setCallSat(String callSat) {
		this.callSat = callSat;
	}

	public String getCallFrom1() {
		return callFrom1;
	}

	public void setCallFrom1(String callFrom1) {
		this.callFrom1 = callFrom1;
	}

	public String getCallFrom2() {
		return callFrom2;
	}

	public void setCallFrom2(String callFrom2) {
		this.callFrom2 = callFrom2;
	}

	public String getCallTo2() {
		return callTo2;
	}

	public void setCallTo2(String callTo2) {
		this.callTo2 = callTo2;
	}

	public String getCallTo1() {
		return callTo1;
	}

	public void setCallTo1(String callTo1) {
		this.callTo1 = callTo1;
	}

	public Long getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(Long campaignId) {
		this.campaignId = campaignId;
	}

	public Integer getHoursStopCalling() {
		return hoursStopCalling;
	}

	public void setHoursStopCalling(Integer hoursStopCalling) {
		this.hoursStopCalling = hoursStopCalling;
	}

	public Integer getDaysBeforeStartCalling() {
		return daysBeforeStartCalling;
	}

	public void setDaysBeforeStartCalling(Integer daysBeforeStartCalling) {
		this.daysBeforeStartCalling = daysBeforeStartCalling;
	}
	
	public Integer getTotMaxAttempts() {
		return totMaxAttempts;
	}

	public void setTotMaxAttempts(Integer totMaxAttempts) {
		this.totMaxAttempts = totMaxAttempts;
	}

	public Integer getMaxAttemptsPerDay() {
		return maxAttemptsPerDay;
	}

	public void setMaxAttemptsPerDay(Integer maxAttemptsPerDay) {
		this.maxAttemptsPerDay = maxAttemptsPerDay;
	}

	public Integer getTimeIntervalBetweenAttempts() {
		return timeIntervalBetweenAttempts;
	}

	public void setTimeIntervalBetweenAttempts(Integer timeIntervalBetweenAttempts) {
		this.timeIntervalBetweenAttempts = timeIntervalBetweenAttempts;
	}

	public Integer getBusDaysBeforeStartCalling() {
		return busDaysBeforeStartCalling;
	}

	public void setBusDaysBeforeStartCalling(Integer busDaysBeforeStartCalling) {
		this.busDaysBeforeStartCalling = busDaysBeforeStartCalling;
	}
}
