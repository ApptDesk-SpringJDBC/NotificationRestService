package com.telappoint.notification.constants;

/**
 * 
 * @author Balaji
 * 
 */
public enum SMSNotifyStatusConstants {

	/**
	 * Below constants related to notify table(notify_sms_status) for SMS
	 */
	NOTIFY_SMS_PENDING(1,"Pending"), 
	NOTIFY_SMS_IN_PROGRESS(2,"Text In Progress"),
	NOTIFY_SMS_SENT(3,"Text Sent"),
	NOTIFY_SMS_CONFIRMED(4,"Text Confirmed"),
	NOTIFY_SMS_CANCELLED(5,"Text Cancelled"),
	NOTIFY_SMS_DONOT_CALL(6,"Do Not Text"),
	NOTIFY_SMS_TERMINATE(7,"Text Terminated"),
	NOTIFY_SMS_SUSPENDED(8,"Text Suspended"),
		
	/**
	 * Below constants related to notify_sms_status table(call_status) for SMS
	 */
	
	NOTIFY_SMS_STATUS_DISPLACEMENT(0,"Displacement"),
	NOTIFY_SMS_STATUS_SENT(1,"Sent"),
	NOTIFY_SMS_STATUS_CONFIRMED(2,"Confirmed"),
	NOTIFY_SMS_STATUS_CANCELLED(3,"Cancelled"),
	NOTIFY_SMS_STATUS_INVALID(4,"Invalid");
	
    private int notifyStatus;
	private String notifyMessage;

	private SMSNotifyStatusConstants(int notifyStatus,String notifyMessage) {
		this.notifyStatus = notifyStatus;
		this.notifyMessage = notifyMessage;
	}

	public int getNotifyStatus() {
		return notifyStatus;
	}

	public String getNotifyMessage() {
		return notifyMessage;
	}
	
	public static class NotifySMSStatusMessage {
		public static String getNotifySMSMessage(int notifyStatus) {
			SMSNotifyStatusConstants[] keys = SMSNotifyStatusConstants.values();
			int _notifyStatus = 0;
			for (SMSNotifyStatusConstants key : keys) {
				_notifyStatus = key.getNotifyStatus();
				if (_notifyStatus == notifyStatus) {
					return key.getNotifyMessage();
				}
			}
			return "";
		}
	}
	
	public static void main(String[] args) {
		String result = NotifySMSStatusMessage.getNotifySMSMessage(1);
		System.out.println(result);
	}
}