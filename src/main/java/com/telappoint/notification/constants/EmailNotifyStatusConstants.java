package com.telappoint.notification.constants;

/**
 * 
 * @author Balaji
 * 
 */
public enum EmailNotifyStatusConstants {

	/**
	 * Below constants related to notify table(notify_email_status) for email
	 */
	NOTIFY_EMAIL_PENDING(1,"Email Pending"), 
	NOTIFY_EMAIL_IN_PROGRESS(2,"Email In Progress"),
	NOTIFY_EMAIL_SENT(3,"Email Sent"),
	NOTIFY_EMAIL_CONFIRED(4,"Email Confirmed"),
	NOTIFY_EMAIL_CANCELLED(5,"Email Cancelled"),
	NOTIFY_EMAIL_DONOT_CALL(6,"Do Not Email"),
	NOTIFY_EMAIL_TERMINATE(7,"Email Terminated"),
	NOTIFY_EMAIL_SUSPENDED(8,"Email Suspended"),
	
	/**
	 * Below constants related to notify_email_status table(call_status) for email
	 */
	
	NOTIFY_EMAIL_STATUS_SENT(1,"Email Sent"),
	NOTIFY_EMAIL_STATUS_CONFIRMED(2,"Email Confirmed"),
	NOTIFY_EMAIL_STATUS_CANCELLED(3,"Email Cancelled"),
	NOTIFY_EMAIL_STATUS_INVALID(4,"Invalid");
	
	private int notifyStatus;
	private String notifyMessage;

	private EmailNotifyStatusConstants(int notifyStatus,String notifyMessage) {
		this.notifyStatus = notifyStatus;
		this.notifyMessage = notifyMessage;
	}

	public int getNotifyStatus() {
		return notifyStatus;
	}

	public String getNotifyMessage() {
		return notifyMessage;
	}
	
	public static class NotifyEmailStatusMessage {
		public static String getNotifyEmailMessage(int notifyStatus) {
			EmailNotifyStatusConstants[] keys = EmailNotifyStatusConstants.values();
			int _notifyStatus = 0;
			for (EmailNotifyStatusConstants key : keys) {
				_notifyStatus = key.getNotifyStatus();
				if (_notifyStatus == notifyStatus) {
					return key.getNotifyMessage();
				}
			}
			return "";
		}
	}
	
	public static void main(String[] args) {
		String result = NotifyEmailStatusMessage.getNotifyEmailMessage(1);
		System.out.println(result);
	}
}