package com.telappoint.notification.model;

public class Token {
	private String token;
	private Long notifyId;
	private String expiryStamp;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	public String getExpiryStamp() {
		return expiryStamp;
	}
	public void setExpiryStamp(String expiryStamp) {
		this.expiryStamp = expiryStamp;
	}
	public Long getNotifyId() {
		return notifyId;
	}
	public void setNotifyId(Long notifyId) {
		this.notifyId = notifyId;
	}
}
