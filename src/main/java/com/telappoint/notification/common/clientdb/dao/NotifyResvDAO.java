package com.telappoint.notification.common.clientdb.dao;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.telappoint.notification.common.model.Campaign;
import com.telappoint.notification.common.model.ClientDeploymentConfig;
import com.telappoint.notification.common.model.ErrorLog;
import com.telappoint.notification.common.model.JdbcCustomTemplate;
import com.telappoint.notification.common.model.NotifyRequest;
import com.telappoint.notification.handlers.exception.TelAppointException;
import com.telappoint.notification.model.CampaignMessageEmail;
import com.telappoint.notification.model.CampaignMessagePhone;
import com.telappoint.notification.model.CampaignMessageSMS;
import com.telappoint.notification.model.DialerSetting;
import com.telappoint.notification.model.DynamicTemplatePlaceHolder;
import com.telappoint.notification.model.FileUpload;
import com.telappoint.notification.model.Notify;
import com.telappoint.notification.model.NotifyPhoneStatus;
import com.telappoint.notification.model.OutboundPhoneLogs;

/**
 * 
 * @author Balaji
 *
 */
public interface NotifyResvDAO {

	public boolean isLocked(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, String device, int timeInterval, ClientDeploymentConfig cdConfig) throws TelAppointException;
	public void updateDialerLockStartTimer(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, String device, String timeZone) throws TelAppointException;
	public void updateDialerLocked(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, String device, String timeZone) throws TelAppointException;
	public void updateDialerUnLocked(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, String device, String timeZone) throws TelAppointException;
	public void updateDialerLockEndTime(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, String device, String timeZone) throws TelAppointException;

	public void getNotifyList(final JdbcCustomTemplate jdbcCustomTemplate, final Logger logger, DialerSetting dialerSetting, String timeZone, String endDate, final String device,
			final List<Notify> notifyList, boolean updateInProgress) throws TelAppointException;

	public DialerSetting getDailerSettings(JdbcCustomTemplate jdbcCustomTemplate, Long campaignId) throws TelAppointException;

	public void updateNotifyStatus(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, final List<Notify> notifyList, final int status) throws TelAppointException;

	public CampaignMessagePhone getCampaignMessagePhone(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, Long campaignId) throws TelAppointException;

	public void getPlaceHolderMap(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, String pkcolumnId, String pkvalue, DynamicTemplatePlaceHolder dynamicTPH,
			final Map<String, String> map) throws TelAppointException;

	public void getDynamicPlaceHolder(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, String category, final DynamicTemplatePlaceHolder dynamicTPH)
			throws TelAppointException;

	public String getPlaceHolderCategories(JdbcCustomTemplate jdbcCustomTemplate, Logger logger) throws TelAppointException;

	public long getAttemptCountByToday(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, long notifyId, String startDateTime, String endDateTime) throws TelAppointException;

	public Notify getNotifyById(JdbcCustomTemplate jdbcCustomTemplate, long notifyId) throws TelAppointException;

	public void savePhoneRecords(JdbcCustomTemplate jdbcCustomTemplate, final List<OutboundPhoneLogs> outboundPhoneList) throws TelAppointException;

	public FileUpload getFileUploadDetails(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, String fileType) throws TelAppointException;

	public void saveNotifyPhoneStatusRecords(JdbcCustomTemplate jdbcCustomTemplate, final List<NotifyPhoneStatus> notifyPhoneStatusList) throws TelAppointException;

	public void updateNotifyPhoneStatusSeconds(JdbcCustomTemplate jdbcCustomTemplate, final List<NotifyPhoneStatus> updatePhoneList) throws TelAppointException;
	
	public void getNotifyWithCustomerIdGreaterThanZero(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, NotifyRequest notifyRequest, final List<Notify> notifyList)
			throws TelAppointException;
	
	public boolean addErrorLog(Logger logger, ErrorLog errorLog);
	
	public CampaignMessageEmail getCampaignMessageEmails(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, Long campaignId) throws TelAppointException;
	
	public int getMaxAttemptId(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, long notifyId) throws TelAppointException;
	public List<Campaign> getCampaigns(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, String langCode, String device, final boolean isFullData) throws TelAppointException;
	
	public CampaignMessageSMS getCampaignMessageSMS(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, Long campaignId) throws TelAppointException;
;
}
