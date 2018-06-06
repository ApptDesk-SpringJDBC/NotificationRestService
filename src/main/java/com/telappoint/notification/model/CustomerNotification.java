package com.telappoint.notification.model;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * 
 * @author Balaji
 * 
 */

@JsonSerialize(include = Inclusion.NON_NULL)
public class CustomerNotification {
	private Long notifyId;

	// only email
	private String email;
	private String ccmail;
	private String bccmail;
	private String city;
	private String firstName;
	private String lastName;

	// for both sms and email
	private String dueDateTime;

	// only sms
	private String phoneNumber;

	// for all sms/email/phone
	private Map<String, String> placeHodersWithValues = new HashMap<String, String>();	
   
	public Long getNotifyId() {
		return notifyId;
	}

	public void setNotifyId(Long notifyId) {
		this.notifyId = notifyId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCcmail() {
		return ccmail;
	}

	public void setCcmail(String ccmail) {
		this.ccmail = ccmail;
	}

	public String getBccmail() {
		return bccmail;
	}

	public void setBccmail(String bccmail) {
		this.bccmail = bccmail;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDueDateTime() {
		return dueDateTime;
	}

	public void setDueDateTime(String dueDateTime) {
		this.dueDateTime = dueDateTime;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Map<String, String> getPlaceHodersWithValues() {
		return placeHodersWithValues;
	}

	public void setPlaceHodersWithValues(Map<String, String> placeHodersWithValues) {
		this.placeHodersWithValues = placeHodersWithValues;
	}

}
