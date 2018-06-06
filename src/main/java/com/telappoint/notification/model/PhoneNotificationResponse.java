package com.telappoint.notification.model;

import java.io.ByteArrayOutputStream;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.telappoint.notification.common.model.BaseResponse;

/**
 * 
 * @author Balaji
 * 
 */
@JsonSerialize(include = Inclusion.NON_NULL)
public class PhoneNotificationResponse extends BaseResponse {
	private ByteArrayOutputStream outputStream;
	
	public ByteArrayOutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(ByteArrayOutputStream outputStream) {
		this.outputStream = outputStream;
	}
}
