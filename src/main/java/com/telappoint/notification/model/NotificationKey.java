package com.telappoint.notification.model;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * 
 * @author Balaji
 * 
 */
@JsonSerialize(include = Inclusion.NON_NULL)
public class NotificationKey {
	// for both sms and email
	private Long compaignId;
	private String subject;
	private String message;

	// only email
	private String fromAddress;
	private String replyToAddress;
	private String enableHTML;

	public Long getCompaignId() {
		return compaignId;
	}

	public void setCompaignId(Long compaignId) {
		this.compaignId = compaignId;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getReplyToAddress() {
		return replyToAddress;
	}

	public void setReplyToAddress(String replyToAddress) {
		this.replyToAddress = replyToAddress;
	}

	public String getEnableHTML() {
		return enableHTML;
	}

	public void setEnableHTML(String enableHTML) {
		this.enableHTML = enableHTML;
	}
	
	@Override
	public String toString() {
		ObjectMapper ow = new ObjectMapper();
		String json = null;
		try {
			json = ow.writeValueAsString(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((compaignId == null) ? 0 : compaignId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NotificationKey other = (NotificationKey) obj;
		if (compaignId == null) {
			if (other.compaignId != null)
				return false;
		} else if (!compaignId.equals(other.compaignId))
			return false;
		return true;
	}
}
