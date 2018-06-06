package com.telappoint.notification.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.telappoint.notification.common.clientdb.dao.NotifyDAO;
import com.telappoint.notification.common.components.CacheComponent;
import com.telappoint.notification.common.components.CommonComponent;
import com.telappoint.notification.common.components.ConnectionPoolUtil;
import com.telappoint.notification.common.components.EmailComponent;
import com.telappoint.notification.common.components.GenericFileUploadComponent;
import com.telappoint.notification.common.constants.CommonDateContants;
import com.telappoint.notification.common.constants.CommonResvDeskConstants;
import com.telappoint.notification.common.constants.EmailNotifyStatusConstants;
import com.telappoint.notification.common.constants.ErrorConstants;
import com.telappoint.notification.common.constants.PropertiesConstants;
import com.telappoint.notification.common.masterdb.dao.MasterDAO;
import com.telappoint.notification.common.model.BaseRequest;
import com.telappoint.notification.common.model.BaseResponse;
import com.telappoint.notification.common.model.Campaign;
import com.telappoint.notification.common.model.Client;
import com.telappoint.notification.common.model.ClientDeploymentConfig;
import com.telappoint.notification.common.model.EmailRequest;
import com.telappoint.notification.common.model.JdbcCustomTemplate;
import com.telappoint.notification.common.model.ResponseModel;
import com.telappoint.notification.common.utils.CoreUtils;
import com.telappoint.notification.common.utils.CustomCsvWriter;
import com.telappoint.notification.common.utils.DateUtils;
import com.telappoint.notification.common.utils.PropertyUtils;
import com.telappoint.notification.constants.NotifyStatusConstants;
import com.telappoint.notification.constants.SMSNotifyStatusConstants;
import com.telappoint.notification.handlers.exception.TelAppointException;
import com.telappoint.notification.model.CampaignEmailResponseAction;
import com.telappoint.notification.model.CampaignMessageEmail;
import com.telappoint.notification.model.CampaignMessageSMS;
import com.telappoint.notification.model.CampaignSMSResponseAction;
import com.telappoint.notification.model.DialerSetting;
import com.telappoint.notification.model.DynamicTemplatePlaceHolder;
import com.telappoint.notification.model.EmailSMSStatusResponse;
import com.telappoint.notification.model.FileUpload;
import com.telappoint.notification.model.FileUploadResponse;
import com.telappoint.notification.model.NotificationResponse;
import com.telappoint.notification.model.Notify;
import com.telappoint.notification.model.SMSConfig;
import com.telappoint.notification.model.Token;
import com.telappoint.notification.service.NotifyService;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Account;
import com.twilio.sdk.resource.instance.Message;

/**
 * 
 * @author Balaji N
 *
 */
@Service
public class NotifyServiceImpl implements NotifyService {

	@Autowired
	private MasterDAO masterDAO;

	@Autowired
	private NotifyDAO notifyDAO;

	@Autowired
	private ConnectionPoolUtil connectionPoolUtil;

	@Autowired
	private CacheComponent cacheComponent;

	@Autowired
	private CommonComponent commonComponent;

	@Autowired
	private EmailComponent emailComponent;

	@Autowired
	private GenericFileUploadComponent genericFileUploadComponent;

	private Map<String, MessageFactory> messageFactoryMap = new ConcurrentHashMap<String, MessageFactory>();

	@Override
	public ResponseEntity<ResponseModel> getDialerLockStatus(String clientCode, Logger logger, String device) throws TelAppointException {
		BaseResponse baseResponse = new BaseResponse();
		Client client = cacheComponent.getClient(logger, clientCode, device, true);
		boolean cache = "Y".equals(client.getCacheEnabled()) ? true : false;
		ClientDeploymentConfig cdConfig = cacheComponent.getClientDeploymentConfig(logger, clientCode, device, cache);
		JdbcCustomTemplate jdbcCustomTemplate = connectionPoolUtil.getJdbcCustomTemplate(logger, client);
		int intervalTime = 60;
		boolean isSuccess = false;
		try {
			String intervalTimeStr = PropertyUtils.getValueFromProperties("DIALER_INTERVAL_TIME_IN_MINS", PropertiesConstants.NOTIFY_SERVICE_REST_WS_PROP.getPropertyFileName());
			if (intervalTimeStr != null) {
				intervalTime = Integer.valueOf(intervalTimeStr);
			}
			isSuccess = notifyDAO.isLocked(jdbcCustomTemplate, logger, device, intervalTime, cdConfig);
		} catch (IOException|DataAccessException ioe) {
			logger.error("Failure in getDialerLock.");
			throw new TelAppointException(ErrorConstants.ERROR_2006.getErrorCode(), ErrorConstants.ERROR_2006.getUserErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
					"Error while write the records to csvWriter", null);
			
		}
		if (isSuccess) {
			baseResponse.setResponseStatus(true);
			baseResponse.setResponseMessage("System can dail now.");
			return new ResponseEntity<ResponseModel>(commonComponent.populateRMDData(logger, baseResponse), HttpStatus.OK);
		} else {
			baseResponse.setResponseStatus(false);
			baseResponse.setResponseMessage("Can't dail now.");
			return new ResponseEntity<ResponseModel>(commonComponent.populateRMDData(logger, baseResponse), HttpStatus.CONFLICT);
		}
	}

	@Override
	public ResponseEntity<ResponseModel> lockDialer(String clientCode, Logger logger, String device) throws TelAppointException {
		BaseResponse baseResponse = new BaseResponse();
		Client client = cacheComponent.getClient(logger, clientCode, device, true);
		boolean cache = "Y".equals(client.getCacheEnabled()) ? true : false;
		ClientDeploymentConfig cdConfig = cacheComponent.getClientDeploymentConfig(logger, clientCode, device, cache);
		JdbcCustomTemplate jdbcCustomTemplate = connectionPoolUtil.getJdbcCustomTemplate(logger, client);
		notifyDAO.updateDialerLockStartTimer(jdbcCustomTemplate, logger, device, cdConfig.getTimeZone());
		notifyDAO.updateDialerLocked(jdbcCustomTemplate, logger, device, cdConfig.getTimeZone());
		baseResponse.setResponseStatus(true);
		baseResponse.setResponseMessage("Dialer locked & updated dialer start time");
		return new ResponseEntity<ResponseModel>(commonComponent.populateRMDData(logger, baseResponse), HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<ResponseModel> updateDialerLockEndTime(String clientCode, Logger logger, String device) throws TelAppointException {
		BaseResponse baseResponse = new BaseResponse();
		Client client = cacheComponent.getClient(logger, clientCode, device, true);
		boolean cache = "Y".equals(client.getCacheEnabled()) ? true : false;
		ClientDeploymentConfig cdConfig = cacheComponent.getClientDeploymentConfig(logger, clientCode, device, cache);
		JdbcCustomTemplate jdbcCustomTemplate = connectionPoolUtil.getJdbcCustomTemplate(logger, client);
		notifyDAO.updateDialerLockEndTime(jdbcCustomTemplate, logger, device, cdConfig.getTimeZone());
		baseResponse.setResponseStatus(true);
		baseResponse.setResponseMessage("Dialer end time updated.");
		return new ResponseEntity<ResponseModel>(commonComponent.populateRMDData(logger, baseResponse), HttpStatus.CREATED);
	}
	
	@Override
	public ResponseEntity<ResponseModel> unLockDialer(String clientCode, Logger logger, String device) throws TelAppointException {
		BaseResponse baseResponse = new BaseResponse();
		Client client = cacheComponent.getClient(logger, clientCode, device, true);
		boolean cache = "Y".equals(client.getCacheEnabled()) ? true : false;
		ClientDeploymentConfig cdConfig = cacheComponent.getClientDeploymentConfig(logger, clientCode, device, cache);

		JdbcCustomTemplate jdbcCustomTemplate = connectionPoolUtil.getJdbcCustomTemplate(logger, client);
		notifyDAO.updateDialerUnLocked(jdbcCustomTemplate, logger, device, cdConfig.getTimeZone());
		baseResponse.setResponseStatus(true);
		baseResponse.setResponseMessage("Dialer unlocked");
		return new ResponseEntity<ResponseModel>(commonComponent.populateRMDData(logger, baseResponse), HttpStatus.CREATED);
	}

	public ByteArrayOutputStream getPhoneNotificationList(String clientCode, Logger logger, String langCode, String device) throws TelAppointException {
		Client client = cacheComponent.getClient(logger, clientCode, device, true);
		boolean cache = "Y".equals(client.getCacheEnabled()) ? true : false;
		ClientDeploymentConfig cdConfig = cacheComponent.getClientDeploymentConfig(logger, clientCode, device, cache);
		StringBuilder resMsg = new StringBuilder();
		JdbcCustomTemplate jdbcCustomTemplate = connectionPoolUtil.getJdbcCustomTemplate(logger, client);
		List<Notify> notifyList = getNotifyList(jdbcCustomTemplate, logger, client.getAppcode(), langCode, device, cdConfig, resMsg);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		CustomCsvWriter csvWriter = new CustomCsvWriter(outputStream, ',', Charset.defaultCharset());
		int size = notifyList.size();
		if (notifyList != null && size > 0) {
			logger.debug("No of notification fetched for send phone notification: " + size);
			populatePhoneNotificationData(jdbcCustomTemplate, notifyList, logger, cdConfig, csvWriter);
		} else {
			logger.debug("No notification fetch");
		}
		return outputStream;
	}

	@Override
	public NotificationResponse sendEmailNotifications(String clientCode, Logger logger, String langCode, String device) throws TelAppointException {
		Client client = cacheComponent.getClient(logger, clientCode, device, true);
		boolean cache = "Y".equals(client.getCacheEnabled()) ? true : false;
		ClientDeploymentConfig cdConfig = cacheComponent.getClientDeploymentConfig(logger, clientCode, device, cache);
		StringBuilder resMsg = new StringBuilder();

		JdbcCustomTemplate jdbcCustomTemplate = connectionPoolUtil.getJdbcCustomTemplate(logger, client);
		List<Notify> notifyList = getNotifyList(jdbcCustomTemplate, logger, client.getAppcode(), langCode, device, cdConfig, resMsg);
		NotificationResponse notificationResponse = new NotificationResponse();
		sendEmailNotifications(jdbcCustomTemplate, logger, device, client.getClientName(), cdConfig, notifyList, notificationResponse);
		return notificationResponse;
	}

	@Override
	public NotificationResponse sendSMSNotifications(String clientCode, Logger logger, String langCode, String device) throws TelAppointException {
		Client client = cacheComponent.getClient(logger, clientCode, device, true);
		boolean cache = "Y".equals(client.getCacheEnabled()) ? true : false;
		ClientDeploymentConfig cdConfig = cacheComponent.getClientDeploymentConfig(logger, clientCode, device, cache);
		StringBuilder resMsg = new StringBuilder();
		JdbcCustomTemplate jdbcCustomTemplate = connectionPoolUtil.getJdbcCustomTemplate(logger, client);
		List<Notify> notifyList = getNotifyList(jdbcCustomTemplate, logger, client.getAppcode(), langCode, device, cdConfig, resMsg);
		SMSConfig smsConfig = masterDAO.getSMSConfig(logger, client.getClientId());
		NotificationResponse notificationResponse = new NotificationResponse();
		sendSMSNotifications(jdbcCustomTemplate, logger, device, client.getClientName(), smsConfig, notifyList, cdConfig, notificationResponse);
		return notificationResponse;
	}

	/**
	 * Used for fetch the notify list based on the dialersetting table
	 * configuration.
	 *
	 * @param clientCode
	 * @param langCode
	 * @param deviceType
	 * @return
	 * @throws TelAppointException
	 */
	public List<Notify> getNotifyList(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, String appCode, String langCode, String device, ClientDeploymentConfig cdConfig,
			StringBuilder responseMsg) throws TelAppointException {
		List<Campaign> campaigns = notifyDAO.getCampaigns(jdbcCustomTemplate, logger, langCode, device, true);
		List<Notify> notifyList = new ArrayList<Notify>();
		if (campaigns == null || campaigns.size() < 1) {
			logger.info("No campaigns, so unable to pull any notifications.");
			return notifyList;
		}

		Map<Long, DialerSetting> dialerSettingMap = notifyDAO.getDailerSettingsMap(jdbcCustomTemplate);
		int notificationsSize = 0;
		DialerSetting dialerSetting = null;
		List<Notify> inNotifyList = new ArrayList<Notify>();
		List<Notify> tempList = null;
		List<Notify> suspendNotifyList = null;
		Set<Long> keySets = dialerSettingMap.keySet();
		for (Long campaignId : keySets) {
			dialerSetting = dialerSettingMap.get(campaignId);
			if (dialerSetting != null) {
				boolean canDialToday = canDialToday(cdConfig, dialerSetting);
				if (canDialToday) {
					if (canDialNow(cdConfig, dialerSetting)) {
						if(isTimeIntervalReached(jdbcCustomTemplate, logger, device, dialerSetting.getTimeIntervalBetweenAttempts(), cdConfig)) {
							String endDate = getNotifyEndDate(dialerSetting, cdConfig.getTimeZone());
							// to remove the duplicates notifications.
							notifyDAO.getNotifyList(jdbcCustomTemplate, logger, appCode, dialerSetting, cdConfig.getTimeZone(), endDate, device, inNotifyList, true);
							if (inNotifyList.size() > 1) {
								if (suspendNotifyList == null || tempList == null) {
									suspendNotifyList = new ArrayList<Notify>();
									tempList = new ArrayList<Notify>();
								}
								removeDuplicateResv(jdbcCustomTemplate, logger, inNotifyList, tempList, suspendNotifyList);
								suspendNotifyList.clear();
								tempList.clear();
							}
							notifyList.addAll(inNotifyList);
							inNotifyList.clear();
						} else {
							responseMsg.append(notificationsSize).append(" ").append(device);
							responseMsg.append(" notification fetched for campaignId: " + campaignId + " Can't dial, because time interval is not reached");
							logger.info(notificationsSize + " " + device + " notifications fetched for campaignId: " + campaignId + " Can't dial, because time interval is not reached");
						}
					} else {
						responseMsg.append(notificationsSize).append(" ").append(device);
						responseMsg.append(" notification fetched for campaignId: " + campaignId + " because it can't be dailNow");
						logger.info(notificationsSize + " " + device + " notifications fetched for campaignId: " + campaignId + " because it can't be dailNow now");
					}
				} else {
					logger.info(notificationsSize + " " + device + " notifications fetched for campaignId: " + campaignId + " because It can not be called today");
					responseMsg.append(notificationsSize).append(" ").append(device);
					responseMsg.append(" notification fetched for campaignId: " + campaignId + " because It can not be called today / now");
				}
			} else {
				logger.info("Dialer Settings is null or Campaign setting not available for campaignId: " + campaignId);
				responseMsg.append(" Dialer Settings is null or Campaign setting not available for campaignId: " + campaignId);
			}
			if (responseMsg.length() > 0) {
				responseMsg.append("|");
			}
		}
		return notifyList;
	}

	private boolean isTimeIntervalReached(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, String device, int timeInterval, ClientDeploymentConfig cdConfig) throws TelAppointException {
		return notifyDAO.isTimeIntervalReached(jdbcCustomTemplate, logger, device, timeInterval, cdConfig);
	}

	private void populatePhoneNotificationData(JdbcCustomTemplate jdbcCustomTemplate, List<Notify> notifyList, Logger logger, ClientDeploymentConfig cdConfig,
			CustomCsvWriter csvWriter) throws TelAppointException {
		Calendar currentCal = DateUtils.getCurrentCalendarByTimeZone(cdConfig.getTimeZone());
		String dateStr = DateUtils.formatGCDateToYYYYMMDD(currentCal);
		String startDateTime = dateStr + " 00:00:00";
		String endDateTime = dateStr + " 23:59:59";
		DialerSetting dialerSettings = null;
		Map<Long, DialerSetting> dialerSettingMap = notifyDAO.getDailerSettingsMap(jdbcCustomTemplate);
		if (notifyList != null) {
			int size = notifyList.size();
			for (int i = 0; i < size; i++) {
				Notify notify = notifyList.get(i);
				long notifyId = notify.getNotifyId();
				int maxAttemptId = notifyDAO.getMaxAttemptId(jdbcCustomTemplate, logger, notifyId);

				String phoneNumber = notify.getHomePhone();
				if (phoneNumber == "" || phoneNumber == null) {
					phoneNumber = notify.getCellPhone();
				}
				if (phoneNumber == "" || phoneNumber == null) {
					phoneNumber = notify.getWorkPhone();
				}

				long attemptsCountToday = notifyDAO.getAttemptCountByToday(jdbcCustomTemplate, logger, notifyId, startDateTime, endDateTime);
				logger.debug("attemptsCountToday::" + attemptsCountToday);
				logger.debug("maxAttemptId::" + maxAttemptId);
				dialerSettings = dialerSettingMap.get(notify.getCampaignId());
				if (maxAttemptId >= dialerSettings.getTotMaxAttempts()) {
					logger.info("total max attempts reached. so no more dials for notifyId :" + notifyId);
					notifyList.remove(i);
					continue;
				} else if (attemptsCountToday >= dialerSettings.getMaxAttemptsPerDay()) {
					logger.info("max attempts per day reached. so no more dials for notifyId :" + notifyId);
					notifyList.remove(i);
					continue;
				}
				String[] columns = new String[2];
				columns[0] = phoneNumber;
				columns[1] = notify.getNotifyId() + "-" + jdbcCustomTemplate.getClientCode() + "-" + phoneNumber + "-"+ size + "-" +(i+1);
				try {
					csvWriter.writeRecord(columns);
				} catch (IOException ioe) {
					logger.error("Write Record to csvWriter failed: " + ioe, ioe);
					throw new TelAppointException(ErrorConstants.ERROR_2000.getErrorCode(), ErrorConstants.ERROR_2000.getUserErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
							"Error while write the records to csvWriter", null);
				}
			}
		}
	}

	private void sendEmailNotifications(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, String device, String clientName, ClientDeploymentConfig cdConfig,
			List<Notify> notifyList, NotificationResponse notificationResponse) throws TelAppointException {
		String clientCode = jdbcCustomTemplate.getClientCode();
		Map<Long, CampaignMessageEmail> campaignMessageMailMap = notifyDAO.getCampaignMessageEmails(jdbcCustomTemplate, logger);
		if (notifyList != null && notifyList.size() > 0) {
			notifyDAO.updateDialerLocked(jdbcCustomTemplate, logger, device, cdConfig.getTimeZone());
			notifyDAO.updateDialerLockStartTimer(jdbcCustomTemplate, logger, device, cdConfig.getTimeZone());
			String categories = notifyDAO.getPlaceHolderCategories(jdbcCustomTemplate, logger);
			String[] categoryArray = categories.split(",");
			
			// store in cache to use it later. to avoid the db call.
			Map<String, DynamicTemplatePlaceHolder> dynamicTPHMap = new HashMap<String,DynamicTemplatePlaceHolder>();
			DynamicTemplatePlaceHolder dynamicTPH = null;
			for (String category : categoryArray) {
				dynamicTPH = new DynamicTemplatePlaceHolder();
				notifyDAO.getDynamicPlaceHolder(jdbcCustomTemplate, logger, category, dynamicTPH);
				dynamicTPHMap.put(category, dynamicTPH);
			}
			
			CampaignMessageEmail campaignEmail = null;
			int successCount = 0;
			int failureCount = 0;
			long notifyId = 0;
			try {
				for (int i = 0; i < notifyList.size(); i++) {	
					try {
						Notify notify = notifyList.get(i);
						campaignEmail = campaignMessageMailMap.get(notify.getCampaignId());
						notifyId = notify.getNotifyId();
						Map<String, String> placeHodersWithValues = new HashMap<String, String>();
						if ("".equals(categories) == false) {
							categoryArray = categories.split(",");
							String pkColumnId = "id";
							Long pkValue = notifyId;
							DynamicTemplatePlaceHolder inDynamicTPH = null;
							for (String category : categoryArray) {
								inDynamicTPH = dynamicTPHMap.get(category);

								if (category.equals("event_result")) {
									pkColumnId = "event_id";
								}

								if (category.equals("resource_result")) {
									pkValue = (long) notify.getResourceId();
								}

								if (category.equals("location_result")) {
									pkValue = (long) notify.getLocationId();
								}

								if (category.equals("service_result")) {
									pkValue = (long) notify.getServiceId();
								}
								notifyDAO.getPlaceHolderMap(jdbcCustomTemplate, logger, pkColumnId, "" + pkValue, inDynamicTPH, placeHodersWithValues);
							}
						}
						String token = notifyDAO.saveAndGetToken(jdbcCustomTemplate, logger, notifyId, device, cdConfig, "");
						placeHodersWithValues.put("firstName", notify.getFirstName());
						placeHodersWithValues.put("lastName", notify.getLastName());

						StringBuilder confirmLink = new StringBuilder(PropertyUtils.getValueFromProperties("CONFIRM_APPT_OR_RESV_SERVICE_URL",
								PropertiesConstants.NOTIFY_SERVICE_REST_WS_PROP.getPropertyFileName()));
						StringBuilder cancelLink = new StringBuilder(PropertyUtils.getValueFromProperties("CANCEL_APPT_OR_RESV_SERVICE_URL",
								PropertiesConstants.NOTIFY_SERVICE_REST_WS_PROP.getPropertyFileName()));
						confirmLink.append("confirm.html?clientCode=").append(clientCode).append("&notifyId=").append(notifyId).append("&token=").append(token);
						
						cancelLink.append("cancel.html?clientCode=").append(clientCode).append("&notifyId=").append(notifyId).append("&token=").append(token);

						placeHodersWithValues.put("confirmLink", confirmLink.toString());
						placeHodersWithValues.put("cancelLink", cancelLink.toString());
						placeHodersWithValues.put("clientName", clientName);
						StringWriter emailBody = commonComponent.processTemplate("" + notify.getCampaignId(), campaignEmail.getMessage(), placeHodersWithValues);
						String emailSubject = campaignEmail.getSubject();
						EmailRequest emailRequest = new EmailRequest();
						emailRequest.setSubject(emailSubject);
						emailRequest.setToAddress(notify.getEmail());
						emailRequest.setEmailBody(emailBody.toString());
						emailComponent.setMailServerPreference(logger, emailRequest);
						emailComponent.sendEmail(logger, emailRequest);
						updateEmailNotifyStatus(jdbcCustomTemplate, logger, token, notify.getNotifyId(), EmailNotifyStatusConstants.NOTIFY_EMAIL_SENT.getNotifyStatus());
						notificationResponse.setNoOfNotificationsSentSuccess(++successCount);
					} catch (Exception e) {
						notificationResponse.setNoOfNotificationsSentFailed(++failureCount);
						logger.error("Error while sending notification for the ID: " + notifyId);
						continue;
					} finally {
						notifyDAO.updateDialerLockEndTime(jdbcCustomTemplate, logger, device, cdConfig.getTimeZone()); 
					}
				}
			} catch (Exception e) {
				logger.error("Error while sending notification for the ID: " + notifyId, e);
			} finally {
				notifyDAO.updateDialerUnLocked(jdbcCustomTemplate, logger, device, cdConfig.getTimeZone());
			}

		} else {
			logger.error("Notification list is empty");
		}
	}

	private void sendSMSNotifications(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, String device, String clientName, SMSConfig smsConfig, List<Notify> notifyList,
			ClientDeploymentConfig cdConfig,NotificationResponse notificationResponse) throws TelAppointException {
		MessageFactory messageFactory = getMessageFactory(smsConfig, jdbcCustomTemplate.getClientCode());
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		List<SqlParameterSource> smsHistoryList = new ArrayList<SqlParameterSource>();
		int batchSize = 100;
		if (notifyList != null && notifyList.size() > 0) {
			notifyDAO.updateDialerLocked(jdbcCustomTemplate, logger, device, cdConfig.getTimeZone());
			notifyDAO.updateDialerLockStartTimer(jdbcCustomTemplate, logger, device, cdConfig.getTimeZone());
			String categories = notifyDAO.getPlaceHolderCategories(jdbcCustomTemplate, logger);
			String[] categoryArray = categories.split(",");
			
			// store in cache to use it later. to avoid the db call.
			Map<String, DynamicTemplatePlaceHolder> dynamicTPHMap = new HashMap<String,DynamicTemplatePlaceHolder>();
			DynamicTemplatePlaceHolder dynamicTPH = null;
			for (String category : categoryArray) {
				dynamicTPH = new DynamicTemplatePlaceHolder();
				notifyDAO.getDynamicPlaceHolder(jdbcCustomTemplate, logger, category, dynamicTPH);
				dynamicTPHMap.put(category, dynamicTPH);
			}
			
			CampaignMessageSMS campaignSMS = null;
			int successCount = 0;
			int failureCount = 0;
			long notifyId = 0;
			try {
				for (int i = 0; i < notifyList.size(); i++) {
					try {
						Notify notify = notifyList.get(i);
						campaignSMS = notifyDAO.getCampaignMessageSMS(jdbcCustomTemplate, logger, notify.getCampaignId());
						notifyId = notify.getNotifyId();
						Map<String, String> placeHodersWithValues = new HashMap<String, String>();
						
						if ("".equals(categories) == false) {
							categoryArray = categories.split(",");
							String pkColumnId = "id";
							Long pkValue = notifyId;
							DynamicTemplatePlaceHolder inDynamicTPH = null;
							for (String category : categoryArray) {
								inDynamicTPH = dynamicTPHMap.get(category);

								if (category.equals("event_result")) {
									pkColumnId = "event_id";
								}

								if (category.equals("resource_result")) {
									pkValue = (long) notify.getResourceId();
								}

								if (category.equals("location_result")) {
									pkValue = (long) notify.getLocationId();
								}

								if (category.equals("service_result")) {
									pkValue = (long) notify.getServiceId();
								}

								notifyDAO.getPlaceHolderMap(jdbcCustomTemplate, logger, pkColumnId, "" + pkValue, inDynamicTPH, placeHodersWithValues);
							}
						}

						placeHodersWithValues.put("firstName", notify.getFirstName());
						placeHodersWithValues.put("lastName", notify.getLastName());
						placeHodersWithValues.put("clientName", clientName);

						StringWriter smsMessage = commonComponent.processTemplate("" + notify.getCampaignId(), campaignSMS.getMessage(), placeHodersWithValues);
						sendSMS(messageFactory, logger, smsMessage.toString(), smsConfig, notify.getCellPhone(), params, smsHistoryList);
						updateSMSSentSuccessStatus(jdbcCustomTemplate, logger, device, notify.getNotifyId(), SMSNotifyStatusConstants.NOTIFY_SMS_SENT.getNotifyStatus(), 1);

						if (batchSize == smsHistoryList.size()) {
							notifyDAO.insertSMSHistory(jdbcCustomTemplate, logger, smsHistoryList);
							smsHistoryList.clear();
						}
						notificationResponse.setNoOfNotificationsSentSuccess(++successCount);
					} catch (Exception e) {
						notificationResponse.setNoOfNotificationsSentFailed(++failureCount);
						logger.error("Error while sending notification for the ID: " + notifyId);
						continue;
					} finally {
						notifyDAO.updateDialerLockEndTime(jdbcCustomTemplate, logger, device, cdConfig.getTimeZone()); 
					}
				}
				notifyDAO.insertSMSHistory(jdbcCustomTemplate, logger, smsHistoryList);
			} catch (Exception e) {
				logger.error("Error while sending notification for the ID: " + notifyId, e);
			} finally {
				notifyDAO.updateDialerUnLocked(jdbcCustomTemplate, logger, device, cdConfig.getTimeZone());
			}
		} else {
			logger.error("Notification list is empty");
		}
	}

	private void sendSMS(MessageFactory messageFactory, Logger logger, String smsMessage, SMSConfig smsConfig, String phoneNumber, List<NameValuePair> params,
			List<SqlParameterSource> smsHistoryList) throws TelAppointException {
		List<String> messages = CoreUtils.splitMessage(smsMessage.trim());
		for (String sms : messages) {
			boolean phoneCheck = (phoneNumber != null && phoneNumber.length() > 9) ? true : false;
			if (phoneCheck) {
				logger.info("phoneNumber: " + phoneNumber);
				try {
					if (messageFactory != null) {	
						params.add(new BasicNameValuePair("To", phoneNumber));
						params.add(new BasicNameValuePair("From", smsConfig.getSmsPhone()));
						params.add(new BasicNameValuePair("Body", sms));
						Message response = messageFactory.create(params);

						String status = null;
						if (response != null) {
							status = response.getStatus();
							MapSqlParameterSource mapSource = new MapSqlParameterSource();
							mapSource.addValue("sid", response.getSid());
							mapSource.addValue("accountSid", response.getAccountSid());
							mapSource.addValue("to", response.getTo());
							mapSource.addValue("from", response.getFrom());
							mapSource.addValue("body", response.getBody());
							mapSource.addValue("status", response.getStatus());
							mapSource.addValue("direction", response.getDirection());
							mapSource.addValue("apiVersion", response.getApiVersion());
							smsHistoryList.add(mapSource);
							logger.info("Status of the sms delivery: " + status);
						} else {
							logger.info("Response is null from sms provider");
						}
					} else {
						logger.info("SMSFactory is null!");
					}
				} catch (TwilioRestException e) {
					logger.error("TwiliRestException: " + e, e);
					// TODO: throw TelAppointmentException
				}
			} else {
				logger.error("Phone number null or wrong phone number");
			}
			params.clear();
		}
	}

	private MessageFactory getMessageFactory(SMSConfig smsConfig, String clientCode) {
		StringBuilder key = new StringBuilder(smsConfig.getAccountSID()).append("|").append(smsConfig.getAuthToken()).append("|").append(smsConfig.getSmsPhone()).append("|")
				.append(clientCode);
		MessageFactory messageFactory = messageFactoryMap.get(key.toString());
		if (messageFactory == null) {
			TwilioRestClient client = new TwilioRestClient(smsConfig.getAccountSID(), smsConfig.getAuthToken());
			messageFactory = client.getAccount().getMessageFactory();
			messageFactoryMap.put(key.toString(), messageFactory);
		}
		return messageFactory;
	}

	/**
	 * update sms sent status to db. 3 - sent
	 */
	public void updateSMSSentSuccessStatus(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, String deviceType, long notifyId, int notifyStatus, int numberOfMessages)
			throws TelAppointException {
		notifyDAO.updateNotifySMSStatus(jdbcCustomTemplate, logger, notifyId, notifyStatus);
		notifyDAO.insertNotifySMSStatus(jdbcCustomTemplate, logger, notifyId, 1, numberOfMessages);
	}

	public EmailSMSStatusResponse updateEmailNotifyStatus(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, String token, long notifyId, int updateNotifyStatus)
			throws TelAppointException {
		EmailSMSStatusResponse emailStatusResponse = new EmailSMSStatusResponse();
		DateFormat sdf = CoreUtils.getSimpleDateFormatYYYYMMDDHHMM();
		Token tokenFromDB = notifyDAO.getTokenByToken(jdbcCustomTemplate, logger, token);

		DataSourceTransactionManager dsTransactionManager = jdbcCustomTemplate.getDataSourceTransactionManager();
		TransactionStatus status = null;
		try {
			if (tokenFromDB != null) {
				Timestamp currentTimeStamp = new Timestamp(System.currentTimeMillis());
				Notify notify = notifyDAO.getNotifyById(jdbcCustomTemplate, notifyId);

				if (notify.getDueDateTime() != null && tokenFromDB.getExpiryStamp() != null) {
					Date dueDateTime = sdf.parse(notify.getDueDateTime());
					Date expiryTimeDate = sdf.parse(tokenFromDB.getExpiryStamp());
					if (expiryTimeDate != null && dueDateTime.before(currentTimeStamp) || expiryTimeDate.before(currentTimeStamp)) {
						emailStatusResponse.setPageEmailHeader(PropertyUtils.getValueFromProperties("email.tokenexpiry.header",
								PropertiesConstants.NOTIFY_SERVICE_REST_WS_PROP.getPropertyFileName()));
						emailStatusResponse.setPageContent(PropertyUtils.getValueFromProperties("email.tokenexpiry.pagecontent",
								PropertiesConstants.NOTIFY_SERVICE_REST_WS_PROP.getPropertyFileName()));
						return emailStatusResponse;
					}
				} else if (tokenFromDB.getExpiryStamp() == null) {
					emailStatusResponse.setPageEmailHeader(PropertyUtils.getValueFromProperties("email.tokenexpiry.header",
							PropertiesConstants.NOTIFY_SERVICE_REST_WS_PROP.getPropertyFileName()));
					emailStatusResponse.setPageContent(PropertyUtils.getValueFromProperties("email.tokenexpiry.pagecontent",
							PropertiesConstants.NOTIFY_SERVICE_REST_WS_PROP.getPropertyFileName()));
					return emailStatusResponse;
				}
			} else {
				logger.error("Token value is not present in system!");
				if (updateNotifyStatus > EmailNotifyStatusConstants.NOTIFY_EMAIL_SENT.getNotifyStatus()) {
					throw new TelAppointException(ErrorConstants.ERROR_2003.getErrorCode(), ErrorConstants.ERROR_2003.getUserErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
							null, null);
				}
			}

			TransactionDefinition def = new DefaultTransactionDefinition();
			status = dsTransactionManager.getTransaction(def);
			if (tokenFromDB != null) {
				if (updateNotifyStatus == EmailNotifyStatusConstants.NOTIFY_EMAIL_SENT.getNotifyStatus()) {
					notifyDAO.updateEmailNotifyStatus(jdbcCustomTemplate, logger, notifyId, 3);
					notifyDAO.insertNotifyEmailStatus(jdbcCustomTemplate, logger, notifyId, 1, 1);
				} else if (updateNotifyStatus == EmailNotifyStatusConstants.NOTIFY_EMAIL_CONFIRED.getNotifyStatus()) {
					notifyDAO.updateEmailNotifyStatus(jdbcCustomTemplate, logger, notifyId, 4);
					notifyDAO.insertNotifyEmailStatus(jdbcCustomTemplate, logger, notifyId, 2, 1);
					prepareEmailStatusResponse(jdbcCustomTemplate, emailStatusResponse, notifyId, "confirm");

				} else if (updateNotifyStatus == EmailNotifyStatusConstants.NOTIFY_EMAIL_CANCELLED.getNotifyStatus()) {
					notifyDAO.updateEmailNotifyStatus(jdbcCustomTemplate, logger, notifyId, 5);
					notifyDAO.insertNotifyEmailStatus(jdbcCustomTemplate, logger, notifyId, 3, 1);
					prepareEmailStatusResponse(jdbcCustomTemplate, emailStatusResponse, notifyId, "cancel");
				} else {
					notifyDAO.updateEmailNotifyStatus(jdbcCustomTemplate, logger, notifyId, updateNotifyStatus);
				}
				dsTransactionManager.commit(status);
			}
		} catch (ParseException | IOException pe) {
			dsTransactionManager.rollback(status);
			throw new TelAppointException(ErrorConstants.ERROR_2004.getErrorCode(), ErrorConstants.ERROR_2004.getUserErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null, null);
		}
		return emailStatusResponse;
	}

	public void prepareEmailStatusResponse(JdbcCustomTemplate jdbcCustomTemplate, EmailSMSStatusResponse emailStatusResponse, long notifyId, String result)
			throws TelAppointException {
		long campainId = notifyDAO.getNotifyById(jdbcCustomTemplate, notifyId).getCampaignId();
		CampaignEmailResponseAction campaignEmailResponseAction = notifyDAO.getDetailsByActionAndCId(jdbcCustomTemplate, campainId, result);
		emailStatusResponse.setPageContent(campaignEmailResponseAction.getPageContent());
		emailStatusResponse.setPageEmailHeader(campaignEmailResponseAction.getPageHeader());
	}

	public boolean canDialToday(ClientDeploymentConfig cdConfig, DialerSetting dialerSettings) throws TelAppointException {
		Calendar cal = DateUtils.getCurrentCalendarByTimeZone(cdConfig.getTimeZone());
		switch (cal.get(Calendar.DAY_OF_WEEK)) {
		case 1:
			if ("Y".equals(dialerSettings.getCallSun()))
				return true;
			break;
		case 2:
			if ("Y".equals(dialerSettings.getCallMon()))
				return true;
			break;
		case 3:
			if ("Y".equals(dialerSettings.getCallTue()))
				return true;
			break;
		case 4:
			if ("Y".equals(dialerSettings.getCallWed()))
				return true;
			break;
		case 5:
			if ("Y".equals(dialerSettings.getCallThu()))
				return true;
			break;
		case 6:
			if ("Y".equals(dialerSettings.getCallFri()))
				return true;
			break;
		case 7:
			if ("Y".equals(dialerSettings.getCallSat()))
				return true;
		}
		return false;
	}

	public boolean canDialNow(ClientDeploymentConfig cdConfig, DialerSetting dialerSettings) {
		String from1 = dialerSettings.getCallFrom1();
		String to1 = dialerSettings.getCallTo1();

		String from2 = null;
		String to2 = null;
		if (dialerSettings.getCallFrom2() != null && dialerSettings.getCallTo2() != null) {
			from2 = dialerSettings.getCallFrom2();
			to2 = dialerSettings.getCallTo2();
		}

		boolean isNotNull2 = (from2 != null && to2 != null) ? true : false;
		if (from1.trim().length() > 0 && to1.trim().length() > 0) {
			if (isTimeBetweenFromTo(from1, to1, cdConfig.getTimeZone()))
				return true;
		}

		if (isNotNull2 && from2.trim().length() > 0 && to2.trim().length() > 0) {
			if (isTimeBetweenFromTo(from2, to2, cdConfig.getTimeZone()))
				return true;
		}
		return false;
	}

	public static boolean isTimeBetweenFromTo(String time1, String time2, String timeZone) {
		// format HH24:MI
		int hour1 = Integer.parseInt(time1.substring(0, 2));
		int min1 = Integer.parseInt(time1.substring(3, 5));
		GregorianCalendar startTime = new GregorianCalendar(TimeZone.getTimeZone(timeZone));
		startTime.set(Calendar.HOUR_OF_DAY, hour1);
		startTime.set(Calendar.MINUTE, min1);
		startTime.set(Calendar.SECOND, 0);
		startTime.set(Calendar.MILLISECOND, 0);

		int hour2 = Integer.parseInt(time2.substring(0, 2));
		int min2 = Integer.parseInt(time2.substring(3, 5));
		GregorianCalendar endTime = new GregorianCalendar(TimeZone.getTimeZone(timeZone));
		endTime.set(Calendar.HOUR_OF_DAY, hour2);
		endTime.set(Calendar.MINUTE, min2);
		endTime.set(Calendar.SECOND, 0);
		endTime.set(Calendar.MILLISECOND, 0);

		GregorianCalendar now = new GregorianCalendar(TimeZone.getTimeZone(timeZone));
		if (now.after(startTime) && now.before(endTime))
			return true;
		return false;
	}

	public void removeDuplicateResv(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, List<Notify> inNotifyList, List<Notify> tempList, List<Notify> suspendNotifyList)
			throws TelAppointException {
		tempList.addAll(inNotifyList);
		// remove duplicate appointment for the same family & same date but
		// keep the earlier appt.
		for (int i = 0; i < tempList.size(); i++) {
			Notify notify1 = (Notify) tempList.get(i);
			for (int j = i + 1; j < tempList.size(); j++) {
				Notify notify2 = (Notify) tempList.get(j);
				String date1 = (notify1.getDueDateTime().toString()).substring(0, 10);
				String date2 = (notify2.getDueDateTime().toString()).substring(0, 10);

				// if appt date and last name and any one of the phone
				// number matches, then cancel the earlier appointment
				if ((date2).equals(date1)
						&& notify2.getEventId() == notify1.getEventId()
						&& (notify2.getLastName()).equals(notify1.getLastName())
						&& (CoreUtils.isStringEqual(notify2.getHomePhone(), notify1.getHomePhone()) || CoreUtils.isStringEqual(notify2.getWorkPhone(), notify1.getWorkPhone()) || CoreUtils
								.isStringEqual(notify2.getCellPhone(), notify1.getCellPhone()))) {
					suspendNotifyList.add(notify2);
					inNotifyList.remove(j);
					logger.info("Removed duplicate resv :" + notify2.getNotifyId() + "," + date2 + "," + notify2.getLastName() + "," + notify2.getHomePhone() + ","
							+ notify2.getWorkPhone() + "," + notify2.getCellPhone());
				}
			}
		}
		notifyDAO.updateNotifyStatus(jdbcCustomTemplate, logger, suspendNotifyList, NotifyStatusConstants.NOTIFY_STATUS_SUSPENDED.getNotifyStatus());
	}

	public String getNotifyEndDate(DialerSetting dialerSetting, String timeZone) throws TelAppointException {
		int minDays = dialerSetting.getDaysBeforeStartCalling();
		int noOfBusDaysBeforeStartCalling = dialerSetting.getBusDaysBeforeStartCalling();
		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone(timeZone));
		// below i get the date from cal instance with timezone and created another Calendar and set the date.
		// because of some issue 
		Date date = cal.getTime();
		cal = new GregorianCalendar();
		cal.setTime(date);
		if(minDays > 0) {
			for (int i = 0; i < minDays; i++) {
				cal.add(Calendar.DATE, 1);
			}
		} else if(noOfBusDaysBeforeStartCalling > 0) {
			setCalendarForBusDaysBeforeStarting(cal, dialerSetting, noOfBusDaysBeforeStartCalling);
		}
		cal.add(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);    
		ThreadLocal<DateFormat> format = DateUtils.getSimpleDateFormat(CommonDateContants.DATETIME_FORMAT_YYYYMMDDHHMMSS_CAP.getValue());
		return format.get().format(cal.getTime());
	}

	private void setCalendarForBusDaysBeforeStarting(Calendar cal, DialerSetting dialerSetting, int noOfBusDaysBeforeStartCalling) {
		noOfBusDaysBeforeStartCalling = noOfBusDaysBeforeStartCalling + 1;
		while (noOfBusDaysBeforeStartCalling > 0) {
			switch (cal.get(Calendar.DAY_OF_WEEK)) {
				case Calendar.MONDAY: {
					if("Y".equals(dialerSetting.getCallMon())) {
						noOfBusDaysBeforeStartCalling--;
					}
					break;
				}
	
				case Calendar.TUESDAY: {
					if("Y".equals(dialerSetting.getCallTue())) {
						noOfBusDaysBeforeStartCalling--;
					}
					break;
				}
	
				case Calendar.WEDNESDAY: {
					if("Y".equals(dialerSetting.getCallWed())) {
						noOfBusDaysBeforeStartCalling--;
					}
					break;
				}
	
				case Calendar.THURSDAY: {
					if("Y".equals(dialerSetting.getCallThu())) {
						noOfBusDaysBeforeStartCalling--;
					}
					break;
				}
	
				case Calendar.FRIDAY: {
					if("Y".equals(dialerSetting.getCallFri())) {
						noOfBusDaysBeforeStartCalling--;
					}
					break;
				}
	
				case Calendar.SATURDAY: {
					if("Y".equals(dialerSetting.getCallSat())) {
						noOfBusDaysBeforeStartCalling--;
					}
					break;
				}
	
				case Calendar.SUNDAY: {
					if("Y".equals(dialerSetting.getCallSun())) {
						noOfBusDaysBeforeStartCalling--;
					}
					break;
				}
			}
			if(noOfBusDaysBeforeStartCalling > 0) {
				cal = DateUtils.addDaysAndGetCalendar(cal, 1);
			}
		}
	}

	public EmailSMSStatusResponse updateEmailNotifyStatus(String clientCode, Logger logger, String token, int notifyStatus, long notifyId) throws TelAppointException {
		String device = CommonResvDeskConstants.EMAIL_REMINDER.getValue();
		Client client = cacheComponent.getClient(logger, clientCode, device, true);
		JdbcCustomTemplate jdbcCustomTemplate = connectionPoolUtil.getJdbcCustomTemplate(logger, client);
		return updateEmailNotifyStatus(jdbcCustomTemplate, logger, token, notifyId, notifyStatus);
	}

	@Override
	public String processSMSConfirmOrCancel(Logger logger, String customerReplyMessage, String custCellNumber, String clientNumber) throws TelAppointException {
		Client client = masterDAO.getClientByPhone(clientNumber);

		JdbcCustomTemplate jdbcCustomTemplate = null;
		ClientDeploymentConfig cdConfig = null;
		try {
			String startTag = PropertyUtils.getValueFromProperties("sms.xml.format.start", PropertiesConstants.NOTIFY_SERVICE_REST_WS_PROP.getPropertyFileName());
			String endTag = PropertyUtils.getValueFromProperties("sms.xml.format.end", PropertiesConstants.NOTIFY_SERVICE_REST_WS_PROP.getPropertyFileName());
			String errorMessage = PropertyUtils.getValueFromProperties("sms.config.incorrect.clientnumber.message",
					PropertiesConstants.NOTIFY_SERVICE_REST_WS_PROP.getPropertyFileName());
			if (client == null) {
				logger.debug("Please check mapping phone number in sms_config table. " + clientNumber);
				return startTag + errorMessage + endTag;
			}
			String clientCode = client.getClientCode();
			logger = getLogger(client.getClientCode(), "INFO");
			String clientName = client.getClientName();
			boolean cache = "Y".equals(client.getCacheEnabled()) ? true : false;
			cdConfig = cacheComponent.getClientDeploymentConfig(logger, clientCode, CommonResvDeskConstants.SMS_REMINDER.getValue(), cache);
			jdbcCustomTemplate = connectionPoolUtil.getJdbcCustomTemplate(logger, client);

			List<Notify> notifyList = notifyDAO.getNotifyDetailsByCellNumber(jdbcCustomTemplate, logger, client.getAppcode(), custCellNumber, cdConfig.getTimeZone());

			long campaignId = 0;
			if (notifyList == null || notifyList.size() == 0) {
				logger.info("No matching notification entry found for this mobile number" + custCellNumber);
				return startTag + errorMessage + endTag;
			}

			Notify notify = null;
			if (notifyList != null && notifyList.size() > 0) {
				notify = notifyList.get(0);
				campaignId = notify.getCampaignId();
				logger.debug("NotifyId:" + notify.getNotifyId());
				if (notify.getNotifyPhoneStatus() >= 5 || notify.getNotifySMSstatus() >= 4 || notify.getNotifyEmailStatus() >= 4) {
					logger.debug("NotifyPhoneStatus:" + notify.getNotifyPhoneStatus());
					logger.debug("NotifySMSStatus:" + notify.getNotifySMSstatus());
					logger.debug("NotifyEmailStatus:" + notify.getNotifyEmailStatus());
					// already confirmed or cancelled. - displacement*/
					insertAndUpdateNotifySMSStatus(jdbcCustomTemplate, logger, notify.getNotifyId(), SMSNotifyStatusConstants.NOTIFY_SMS_STATUS_DISPLACEMENT.getNotifyStatus(), 6,
							clientCode, 1);
					String morethenMessage = PropertyUtils.getValueFromProperties("sms.morethan.one.confirmorcancel.message",
							PropertiesConstants.NOTIFY_SERVICE_REST_WS_PROP.getPropertyFileName());
					return startTag + morethenMessage + endTag;
				}
				String appCode = client.getAppcode();
				List<Notify> notifySentStatusList = notifyDAO.getNotifyDetailsByCellNumberWithSMSStatusSent(jdbcCustomTemplate, logger, appCode, custCellNumber,
						cdConfig.getTimeZone());

				if (notifySentStatusList != null && notifySentStatusList.size() > 1) {
					notify = notifySentStatusList.get(0);

					if ("appt".equals(appCode)) {
						sendEmailTOResource(jdbcCustomTemplate, logger, notify, customerReplyMessage);
					} else {
						// TODO: for resv desk
					}

				} else {
					logger.error("Sending a generic message to customer because of more then one message as pending for confirm/cancel");
					campaignId = 1;
				}
			}

			String customerName = notify.getFirstName();
			logger.info("clientCode:" + clientCode + " customerName:" + customerName + " clientName:" + clientName);
			if (customerName == null) {
				logger.debug("Dear Customer, You are not recognized. Please contact your Vendor. Thank you." + clientNumber);
				String incorrectCustNumberMessage = PropertyUtils.getValueFromProperties("sms.config.incorrect.custnumber.message",
						PropertiesConstants.NOTIFY_SERVICE_REST_WS_PROP.getPropertyFileName());
				return startTag + incorrectCustNumberMessage + endTag;
			}

			CampaignMessageSMS campaignMessageSms = notifyDAO.getCampaignPhoneSms(jdbcCustomTemplate, logger, campaignId);
			CampaignSMSResponseAction campaignSMSResponseAction = notifyDAO.getCampaignSMSResponseAction(jdbcCustomTemplate, logger, campaignId);

			if (campaignSMSResponseAction == null) {
				logger.error("campaign sms response action table not configured properly for campaignIdss: " + campaignId);
				throw new TelAppointException(ErrorConstants.ERROR_2005.getErrorCode(), ErrorConstants.ERROR_2005.getUserErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
						"campaign sms response action table not configured properly", new BaseRequest());
			} else {
				String confirmationMessage = "";
				String action = campaignSMSResponseAction.getAction();
				String inputValueFromDB = campaignSMSResponseAction.getInputValue();

				long notifyId = notify.getNotifyId();
				if (campaignSMSResponseAction != null) {
					if ("CONFIRM".equalsIgnoreCase(action) && inputValueFromDB.equals(customerReplyMessage.trim())) {
						insertAndUpdateNotifySMSStatus(jdbcCustomTemplate, logger, notifyId, SMSNotifyStatusConstants.NOTIFY_SMS_STATUS_CONFIRMED.getNotifyStatus(), 4, clientCode,
								1);
						confirmationMessage = campaignMessageSms.getInputResponse1();
					} else if ("CANCEL".equalsIgnoreCase(action) && inputValueFromDB.equals(customerReplyMessage.trim())) {
						// notify_sms_status.sms_status = 3 - Cancelled */

						insertAndUpdateNotifySMSStatus(jdbcCustomTemplate, logger, notify.getNotifyId(), SMSNotifyStatusConstants.NOTIFY_SMS_STATUS_CANCELLED.getNotifyStatus(), 5,
								clientCode, 1);
						confirmationMessage = campaignMessageSms.getInputResponse2();
					} else {
						// notify_sms_status.sms_status = 4 - Invalid */
						insertAndUpdateNotifySMSStatus(jdbcCustomTemplate, logger, notify.getNotifyId(), SMSNotifyStatusConstants.NOTIFY_SMS_STATUS_INVALID.getNotifyStatus(), 6,
								clientCode, 1);
						confirmationMessage = campaignMessageSms.getElseResponse();
					}
				}
				if (confirmationMessage == null || "".equals(confirmationMessage)) {
					logger.error("confirmation OR cancellation sms response message not configured.(The configuration values inputResponse1/inputResponse2)");
					throw new TelAppointException(ErrorConstants.ERROR_2005.getErrorCode(), ErrorConstants.ERROR_2005.getUserErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
							"Confirm/Cancel SMS message is null", new BaseRequest());
				}
				return addDynamicContentToMessage(confirmationMessage, customerName, clientName, startTag, endTag);
			}
		} catch (IOException e) {
			logger.error("Error:" + e, e);
			throw new TelAppointException(ErrorConstants.ERROR_2005.getErrorCode(), ErrorConstants.ERROR_2005.getUserErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
					e.toString(), new BaseRequest());
		}
	}

	private void sendEmailTOResource(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, Notify notify, String customerReplyAction) throws TelAppointException {
		int resourceId = notify.getResourceId();
		String toAddress = notifyDAO.getResourceEmail(jdbcCustomTemplate, logger, resourceId);
		logger.debug("NotifyId (fetch where sms status =3 (sent)):" + notify.getNotifyId());
		if (toAddress != null) {
			logger.debug("Email Id for the notifyId:" + notify.getNotifyId() + " is " + toAddress);
			boolean isValidEmail = CoreUtils.isValidEmailAddress(toAddress);
			try {
				if (isValidEmail) {
					String subject = PropertyUtils.getValueFromProperties("email.subject", PropertiesConstants.NOTIFY_SERVICE_REST_WS_PROP.getPropertyFileName());
					StringBuilder bodyText = new StringBuilder(PropertyUtils.getValueFromProperties("email.body",
							PropertiesConstants.NOTIFY_SERVICE_REST_WS_PROP.getPropertyFileName()));
					bodyText.append("<BR>");
					bodyText.append("Notification ID: " + notify.getIncludeAudio4() + " <BR>");
					bodyText.append("Employee Name: " + notify.getFirstName() + " " + notify.getLastName() + " <BR>");
					bodyText.append("Cell Phone: " + notify.getCellPhone() + " <BR>");
					bodyText.append("Onsite Date: " + notify.getDueDateTime() + "<BR>");
					bodyText.append("Onsite Location: " + notify.getIncludeAudio1() + "<BR>");
					String message = "1".equals(customerReplyAction.trim()) ? "Confirmed" : "";
					if ("".equals(message)) {
						message = "2".equals(customerReplyAction.trim()) ? "Conflicted" : "";
					}
					if ("".equals(message)) {
						message = "N/A";
					}
					bodyText.append("Response Status: " + message + " <BR>");
					try {
						EmailRequest emailRequest = new EmailRequest();
						emailRequest.setSubject(subject);
						emailRequest.setToAddress(notify.getEmail());
						emailRequest.setEmailBody(bodyText.toString());
						emailComponent.setMailServerPreference(logger, emailRequest);
						emailComponent.sendEmail(logger, emailRequest);
					} catch (Exception e) {
						logger.error("Error:" + e, e);
					}
				} else {
					logger.error("Resource email id is not valid.");
				}
			} catch (IOException ie) {
				logger.error("Error while sending email to doctor/resource");
			}
		} else {
			logger.debug("resource for the notifyId:" + notify.getNotifyId() + " is " + notify.getFirstName() + " " + notify.getLastName());
		}

	}

	private void insertAndUpdateNotifySMSStatus(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, long notifyId, int smsStatus, int notifystatus, String clientCode,
			int numberOfSMS) throws TelAppointException {
		notifyDAO.insertNotifySMSStatus(jdbcCustomTemplate, logger, notifyId, smsStatus, 1);
		if (notifystatus < 6) {
			notifyDAO.updateNotifySMSStatus(jdbcCustomTemplate, logger, notifyId, notifystatus);
			notifyDAO.updateNotifyStatus(jdbcCustomTemplate, logger, notifyId, 3);
		}
	}

	private String addDynamicContentToMessage(String message, String custName, String clientName, String startTag, String endTag) {
		message = message.replaceAll("firstName", custName);
		message = message.replaceAll("clientName", clientName);
		return startTag + message + endTag;
	}

	public ResponseEntity<ResponseModel> handleException(Logger logger, Exception tae) {
		return commonComponent.handleException(logger, tae);
	}

	@Override
	public Logger getLogger(String clientCode, String logLevel) throws TelAppointException {
		return commonComponent.getLogger(clientCode, logLevel);
	}

	@Override
	public Logger changeLogLevel(String clientCode, String logLevel) throws TelAppointException {
		return commonComponent.changeLogLevel(clientCode, logLevel);
	}
	
	@Override
	public BaseResponse updateSMSHistory(String clientCode, Logger logger, String device) throws TelAppointException {
		BaseResponse baseResponse = new BaseResponse();
		Client client = cacheComponent.getClient(logger, clientCode, device, true);
		SMSConfig smsConfig = masterDAO.getSMSConfig(logger, client.getClientId());
		JdbcCustomTemplate jdbcCustomTemplate = connectionPoolUtil.getJdbcCustomTemplate(logger, client);
		TwilioRestClient twilioClient = new TwilioRestClient(smsConfig.getAccountSID(), smsConfig.getAuthToken());
		List<String> smsSidList = notifyDAO.getSMSHisotryIdList(jdbcCustomTemplate, logger);
		List<SqlParameterSource> smsHistoryList = new ArrayList<SqlParameterSource>();
		Account account = twilioClient.getAccount();
		Message response = null;
		for(String sid: smsSidList) {
			response = account.getMessage(sid);
			MapSqlParameterSource mapSource = new MapSqlParameterSource();
			mapSource.addValue("status", response.getStatus());
			mapSource.addValue("price", response.getPrice());
			mapSource.addValue("direction", response.getDirection());
			//mapSource.addValue("dateUpdated", response.getDateUpdated());
			//mapSource.addValue("dateSent", response.getDateSent());
			mapSource.addValue("sid", response.getSid());
			smsHistoryList.add(mapSource);
			logger.info("Status of the SID: ["+response.getSid()+"], Status:" + response.getStatus());
			logger.debug("SID: ["+response.getSid()+"], JSON response: "+response.toJSON());
		}
		notifyDAO.updateSMSHistory(jdbcCustomTemplate, logger, smsHistoryList);
		return baseResponse;
	}

	@Override
	public FileUploadResponse processThePhoneLogFile(String clientCode, Logger logger, long InFileSize, String device, String transId, InputStream inputStream, String fileType)
			throws TelAppointException {
		FileUploadResponse fileUploadResponse = null;
		Client client = cacheComponent.getClient(logger, clientCode, device, true);
		boolean cache = "Y".equals(client.getCacheEnabled()) ? true : false;
		ClientDeploymentConfig cdConfig = cacheComponent.getClientDeploymentConfig(logger, clientCode, device, cache);
		JdbcCustomTemplate jdbcCustomTemplate = connectionPoolUtil.getJdbcCustomTemplate(logger, client);

		FileUpload fileUpload = notifyDAO.getFileUploadDetails(jdbcCustomTemplate, logger, "CSV");
		long fileSize = fileUpload.getMaxFileSizeKB();
		if (fileType.equalsIgnoreCase(fileUpload.getFileType()) || "gpg".equalsIgnoreCase(fileType)) {
			if (fileSize < InFileSize) {
				fileUploadResponse = new FileUploadResponse();
				fileUploadResponse.setErrorResponse("File size should not be greater than " + fileSize);
				return fileUploadResponse;
			}
			fileUploadResponse = genericFileUploadComponent.processPhoneLogFile(jdbcCustomTemplate, logger, inputStream, cdConfig);
		}
		return fileUploadResponse;
	}
	

	public ResponseModel populateRMDSuccessData(Logger logger, Object data) {
		return commonComponent.populateRMDData(logger, data);
	}
}
