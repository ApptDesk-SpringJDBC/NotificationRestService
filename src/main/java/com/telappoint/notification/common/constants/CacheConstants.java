package com.telappoint.notification.common.constants;

/**
 * 
 * @author Balaji N
 *
 * Cache table by below key name with client code. [key|clientCode]= any object or Map
 */
public enum CacheConstants {

	// Master db table keys
	CLIENT("CLIENT"), 
	CLIENT_DEPLOYMENT_CONFIG("CLIENT_DEPLOYMENT_CONFIG"),

	CAMPAIGN_MESSAGE_EMAIL("CAMPAIGN_MESSAGE_EMAIL"),
	CAMPAIGN_MESSAGE_SMS("CAMPAIGN_MESSAGE_SMS"),
	CAMPAIGN_MESSAGE_PHONE("CAMPAIGN_MESSAGE_PHONE");

	private String value;

	private CacheConstants(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
