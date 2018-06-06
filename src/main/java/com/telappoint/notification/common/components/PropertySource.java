package com.telappoint.notification.common.components;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertySource {

	@Value("${app.jdbc.clientdb.username}")
	private String clientUserName;

	@Value("${app.jdbc.clientdb.password}")
	private String clientPassword;

	public String getClientPassword() {
		return clientPassword;
	}

	public void setClientPassword(String clientPassword) {
		this.clientPassword = clientPassword;
	}

	public String getClientUserName() {
		return clientUserName;
	}

	public void setClientUserName(String clientUserName) {
		this.clientUserName = clientUserName;
	}

}
