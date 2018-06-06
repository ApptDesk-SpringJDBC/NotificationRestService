package com.telappoint.notification.constants;

/**
 * 
 * @author Balaji
 * 
 */
public enum NotifyStatusConstants {

	/**
	 * Below constants used to notify table (notify_status) for email, SMS and Phone
	 * NOTE: notifyStatus - 0 or 1 (Pending)
	 */
	NOTIFY_STATUS_PENDING_0(0,"Pending"), 
	NOTIFY_STATUS_PENDING(1,"Pending"), 
	NOTIFY_STATUS_IN_PROGRESS(2,"In Progress"),
	NOTIFY_STATUS_COMPLETE(3,"Complete"),
	NOTIFY_STATUS_DO_NOT_CALL(4,"Do not call"),
	NOTIFY_STATUS_TERMINATE(5,"Terminate"),
	NOTIFY_STATUS_SUSPENDED(6,"Suspended");
	
    private int notifyStatus;
	private String notifyMessage;

	private NotifyStatusConstants(int notifyStatus,String notifyMessage) {
		this.notifyStatus = notifyStatus;
		this.notifyMessage = notifyMessage;
	}

	public int getNotifyStatus() {
		return notifyStatus;
	}

	public String getNotifyMessage() {
		return notifyMessage;
	}
	
	public static class NotifyStatusMessage {
		public static String getNotifyMessage(int notifyStatus) {
			NotifyStatusConstants[] keys = NotifyStatusConstants.values();
			int _notifyStatus = 0;
			for (NotifyStatusConstants key : keys) {
				_notifyStatus = key.getNotifyStatus();
				if (_notifyStatus == notifyStatus) {
					return key.getNotifyMessage();
				}
			}
			return "";
		}
	}
	
	public static void main(String[] args) {
		String result = NotifyStatusMessage.getNotifyMessage(1);
		System.out.println(result);
	}
}