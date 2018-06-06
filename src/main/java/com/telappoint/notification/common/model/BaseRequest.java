package com.telappoint.notification.common.model;
/**
 * 
 * @author Balaji N
 *
 */
public class BaseRequest {
	private String clientCode;
	private String transId;
	private String device;
	
	public String getClientCode() {
		return clientCode;
	}
	public void setClientCode(String clientCode) {
		this.clientCode = clientCode;
	}
	public String getTransId() {
		return transId;
	}
	public void setTransId(String transId) {
		this.transId = transId;
	}
	@Override
	public String toString() {
		return "BaseRequest [clientCode=" + clientCode + ", transId=" + transId + "]";
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
}
