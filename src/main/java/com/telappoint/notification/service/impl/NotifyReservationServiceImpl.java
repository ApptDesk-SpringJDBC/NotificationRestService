package com.telappoint.notification.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.telappoint.notification.common.clientdb.dao.NotifyResvDAO;
import com.telappoint.notification.common.components.CacheComponent;
import com.telappoint.notification.common.components.CommonComponent;
import com.telappoint.notification.common.components.ConnectionPoolUtil;
import com.telappoint.notification.common.constants.CommonDateContants;
import com.telappoint.notification.common.constants.CommonResvDeskConstants;
import com.telappoint.notification.common.constants.ErrorConstants;
import com.telappoint.notification.common.masterdb.dao.MasterDAO;
import com.telappoint.notification.common.model.BaseResponse;
import com.telappoint.notification.common.model.Campaign;
import com.telappoint.notification.common.model.Client;
import com.telappoint.notification.common.model.ClientDeploymentConfig;
import com.telappoint.notification.common.model.JdbcCustomTemplate;
import com.telappoint.notification.common.model.ResponseModel;
import com.telappoint.notification.common.utils.CoreUtils;
import com.telappoint.notification.common.utils.CustomCsvWriter;
import com.telappoint.notification.common.utils.DateUtils;
import com.telappoint.notification.constants.NotifyStatusConstants;
import com.telappoint.notification.handlers.exception.TelAppointException;
import com.telappoint.notification.model.CampaignMessageEmail;
import com.telappoint.notification.model.CampaignMessageSMS;
import com.telappoint.notification.model.CustomerNotification;
import com.telappoint.notification.model.DialerSetting;
import com.telappoint.notification.model.DynamicTemplatePlaceHolder;
import com.telappoint.notification.model.NotificationKey;
import com.telappoint.notification.model.NotificationResponse;
import com.telappoint.notification.model.Notify;
import com.telappoint.notification.model.SMSConfig;
import com.telappoint.notification.service.NotifyReservationService;

/**
 * 
 * @author Balaji N
 *
 */
@Service
public class NotifyReservationServiceImpl implements NotifyReservationService {

	@Autowired
	private MasterDAO masterDAO;

	@Autowired
	private NotifyResvDAO notifyDAO;

	@Autowired
	private ConnectionPoolUtil connectionPoolUtil;

	@Autowired
	private CacheComponent cacheComponent;

	@Autowired
	private CommonComponent commonComponent;

	@Override
	public ResponseEntity<ResponseModel> lockDialer(String clientCode, Logger logger, String device) throws TelAppointException {
		BaseResponse baseResponse = new BaseResponse();
		JdbcCustomTemplate jdbcCustomTemplate = null;
		ClientDeploymentConfig cdConfig = null;

		Client client = cacheComponent.getClient(logger, clientCode, device, true);
		boolean cache = "Y".equals(client.getCacheEnabled()) ? true : false;
		cdConfig = cacheComponent.getClientDeploymentConfig(logger, clientCode, device, cache);
		jdbcCustomTemplate = connectionPoolUtil.getJdbcCustomTemplate(logger, client);
		int intervalTime = 60;
		boolean isSuccess = notifyDAO.isLocked(jdbcCustomTemplate, logger, device, intervalTime, cdConfig);

		if (isSuccess) {
			baseResponse.setResponseStatus(true);
			baseResponse.setResponseMessage("System can dail now.");
			return new ResponseEntity<ResponseModel>(commonComponent.populateRMDData(logger, baseResponse), HttpStatus.ACCEPTED);
		} else {
			baseResponse.setResponseStatus(false);
			baseResponse.setResponseMessage("Can't dail now.");
			return new ResponseEntity<ResponseModel>(commonComponent.populateRMDData(logger, baseResponse), HttpStatus.CONFLICT);
		}
	}

	public ResponseModel populateRMDSuccessData(Logger logger, Object data) {
		return commonComponent.populateRMDData(logger, data);
	}

	@Override
	public ResponseEntity<ResponseModel> unLockDialer(String clientCode, Logger logger, String device) throws TelAppointException {
		BaseResponse baseResponse = new BaseResponse();
		JdbcCustomTemplate jdbcCustomTemplate = null;
		ClientDeploymentConfig cdConfig = null;
		Client client = cacheComponent.getClient(logger, clientCode, device, true);
		boolean cache = "Y".equals(client.getCacheEnabled()) ? true : false;
		cdConfig = cacheComponent.getClientDeploymentConfig(logger, clientCode, device, cache);

		jdbcCustomTemplate = connectionPoolUtil.getJdbcCustomTemplate(logger, client);
		notifyDAO.updateDialerUnLocked(jdbcCustomTemplate, logger, device, cdConfig.getTimeZone());
		notifyDAO.updateDialerLockEndTime(jdbcCustomTemplate, logger, device, cdConfig.getTimeZone());
		baseResponse.setResponseStatus(true);
		baseResponse.setResponseMessage("Dialer unlocked");
		return new ResponseEntity<ResponseModel>(commonComponent.populateRMDData(logger, baseResponse), HttpStatus.ACCEPTED);
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
	public NotificationResponse getNotifyList(String clientCode, Logger logger, String langCode, String device) throws TelAppointException {
		logger.info("getNotifyList - " + device);
		JdbcCustomTemplate jdbcCustomTemplate = null;
		ClientDeploymentConfig cdConfig = null;

		NotificationResponse notificationResponse = new NotificationResponse();
		Map<NotificationKey, List<CustomerNotification>> notificationInfoMap = null;
		ByteArrayOutputStream outputStream = null;
		CustomCsvWriter csvWriter = null;

		if (CommonResvDeskConstants.PHONE_REMINDER.getValue().equals(device)) {
			outputStream = new ByteArrayOutputStream();
			csvWriter = new CustomCsvWriter(outputStream, ',', Charset.defaultCharset());
		} else {
			notificationInfoMap = new HashMap<NotificationKey, List<CustomerNotification>>();
		}

		Client client = cacheComponent.getClient(logger, clientCode, device, true);
		boolean cache = "Y".equals(client.getCacheEnabled()) ? true : false;
		cdConfig = cacheComponent.getClientDeploymentConfig(logger, clientCode, device, cache);

		jdbcCustomTemplate = connectionPoolUtil.getJdbcCustomTemplate(logger, client);
		List<Campaign> campaigns = notifyDAO.getCampaigns(jdbcCustomTemplate, logger, langCode, device, true);

		if (campaigns == null || campaigns.size() < 1) {
			logger.info("No campaigns to process. I.e No notifications.");
			notificationResponse.setResponseStatus(false);
			notificationResponse.setResponseMessage("No campaigns to process. I.e No notifications");
			return notificationResponse;
		}
		notifyDAO.updateDialerLocked(jdbcCustomTemplate, logger, device, cdConfig.getTimeZone());
		notifyDAO.updateDialerLockStartTimer(jdbcCustomTemplate, logger, device, cdConfig.getTimeZone());

		DialerSetting dialerSetting = null;
		int notificationsSize = 0;
		StringBuilder responseMsg = new StringBuilder();
		for (Campaign campaign : campaigns) {
			Long campaignId = campaign.getCampaignId();
			dialerSetting = notifyDAO.getDailerSettings(jdbcCustomTemplate, campaignId);
			List<Notify> notifyList = new ArrayList<Notify>();
			if (dialerSetting != null) {
				boolean canDialToday = canDialToday(cdConfig, dialerSetting);
				if (canDialToday) {
					if (canDialNow(cdConfig, dialerSetting)) {
						String endDate = getNotifyEndDate(dialerSetting, cdConfig.getTimeZone());

						// to remove the duplicates notifications.
						List<Notify> inNotifyList = new ArrayList<Notify>();
						notifyDAO.getNotifyList(jdbcCustomTemplate, logger, dialerSetting, cdConfig.getTimeZone(), endDate, device, inNotifyList, false);
						removeDuplicateResv(jdbcCustomTemplate, logger, notifyList);
						inNotifyList.clear();
						notifyDAO.getNotifyList(jdbcCustomTemplate, logger, dialerSetting, cdConfig.getTimeZone(), endDate, device, inNotifyList, true);
						notifyList.addAll(inNotifyList);
						notificationsSize = notificationsSize + notifyList.size();
					} else {
						responseMsg.append(notificationsSize).append(" ").append(device).append( " notification fetched for campaignId: " + campaignId+" because it can't be dailNow");
						logger.debug(notificationsSize+" "+device+" notification fetched for campaignId: " + campaignId+" because it can't be dailNow");
					}
				} else {
					logger.info(notificationsSize+ " "+device+" notification fetched for campaignId: " + campaignId+" because It can not be called today / now");
					responseMsg.append(notificationsSize).append(" ").append(device).append( " notification fetched for campaignId: " + campaignId+" because It can not be called today / now");
				}

				
				if (notificationsSize > 0) {
					notificationResponse.setClientCode(clientCode);
					notificationResponse.setClientName(client.getClientName());
					responseMsg.append(notificationsSize).append(" ").append(device).append( " notification fetched for campaignId: " + campaignId);
					notificationResponse.setNoOfNotificationsFetched(notificationsSize);
					if (CommonResvDeskConstants.EMAIL_REMINDER.getValue().equals(device)) {
						prepareEmailNotificationResponse(jdbcCustomTemplate, logger, notifyList, notificationInfoMap);
					} else if (CommonResvDeskConstants.SMS_REMINDER.getValue().equals(device)) {
						prepareSMSNotificationResponse(jdbcCustomTemplate, logger, notifyList, notificationInfoMap);
						notificationResponse.setAuthDetailsSMS(getSMSAuthDetails(logger, client.getClientId()));
					} else if (CommonResvDeskConstants.PHONE_REMINDER.getValue().equals(device)) {
						preparePhoneNotificationResponse(jdbcCustomTemplate, logger, notifyList, cdConfig, dialerSetting, csvWriter, responseMsg, notificationResponse);
					}
				} 
				
			} else {
				logger.info("Dialer Settings is null or Campaign setting not available for campaignId: " + campaignId);
				responseMsg.append(notificationsSize).append(" notification fetched because of dialer settings is not proper setup or Campaign setting not available for campaignId: " + campaignId);
			}
			responseMsg.append("|");
		}
		notificationResponse.setNoOfNotificationsFetched(notificationsSize);
		notificationResponse.setResponseMessage(responseMsg.toString());
		if (CommonResvDeskConstants.PHONE_REMINDER.getValue().equals(device)) {
			notificationResponse.setOutputStream(outputStream);
		} else {
			notificationResponse.setNotificationInfo(notificationInfoMap);
		}
		return notificationResponse;
	}

	private void preparePhoneNotificationResponse(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, List<Notify> notifyList, ClientDeploymentConfig cdConfig,
			DialerSetting dialerSettings, CustomCsvWriter csvWriter, StringBuilder responseMsg, NotificationResponse notificationResponse) throws TelAppointException {
		Calendar currentCal = DateUtils.getCurrentCalendarByTimeZone(cdConfig.getTimeZone());
		String dateStr = DateUtils.formatGCDateToYYYYMMDD(currentCal);
		String startDateTime = dateStr + " 00:00:00";
		String endDateTime = dateStr + " 23:59:59";

		if (notifyList != null) {
			for (int i = 0; i < notifyList.size(); i++) {
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

				if (maxAttemptId >= dialerSettings.getTotMaxAttempts()) {
					logger.debug("total max attempts reached. so no more dials for notifyId :" + notifyId);
					if(responseMsg.length() > 0) {
						responseMsg.append(",").append("total max attempts reached. so no more dials for notifyId:").append(notifyId);
					}
					notifyList.remove(i);
					continue;
				} else if (attemptsCountToday >= dialerSettings.getMaxAttemptsPerDay()) {
					logger.debug("max attempts per day reached. so no more dials for notifyId :" + notifyId);
					if(responseMsg.length() > 0) {
						responseMsg.append(",").append("max attempts per day reached. so no more dials for notifyId:").append(notifyId);
					}
					notifyList.remove(i);
					continue;
				}

				String[] columns = new String[2];
				columns[0] = phoneNumber;
				columns[1] = notify.getNotifyId() + "-" + notificationResponse.getClientCode() + "-" + phoneNumber;
				try {
					csvWriter.writeRecord(columns);
				} catch (IOException ioe) {
					throw new TelAppointException(ErrorConstants.ERROR_2000.getErrorCode(), ErrorConstants.ERROR_2000.getUserErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
							"Error while write the records to csvWriter", null);
				}
			}
		}
	}

	private void prepareSMSNotificationResponse(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, List<Notify> notifyList,
			Map<NotificationKey, List<CustomerNotification>> notificationInfoMap) throws TelAppointException {
		if (notifyList != null && notifyList.size() > 0) {
			CustomerNotification customerNotification = null;

			List<CustomerNotification> customerNotificationList = null;
			for (int i = 0; i < notifyList.size(); i++) {
				customerNotification = new CustomerNotification();
				Notify notify = notifyList.get(i);
				CampaignMessageSMS campaignsms = notifyDAO.getCampaignMessageSMS(jdbcCustomTemplate, logger, notify.getCampaignId());

				// Above query need to changed - need to use cache.
				NotificationKey notificationKey = new NotificationKey();
				notificationKey.setCompaignId(notify.getCampaignId());

				// CustomerNotification bean populate
				Map<String, String> placeHodersWithValues = new HashMap<String, String>();
				String categories = notifyDAO.getPlaceHolderCategories(jdbcCustomTemplate, logger);
				if ("".equals(categories) == false) {
					String[] categoryArray = categories.split(",");
					for (String category : categoryArray) {
						DynamicTemplatePlaceHolder dynamicTPH = new DynamicTemplatePlaceHolder();
						notifyDAO.getDynamicPlaceHolder(jdbcCustomTemplate, logger, category, dynamicTPH);
						notifyDAO.getPlaceHolderMap(jdbcCustomTemplate, logger, "id", "" + notify.getNotifyId(), dynamicTPH, placeHodersWithValues);
					}
				}
				customerNotification.setFirstName(notify.getFirstName());
				customerNotification.setLastName(notify.getLastName());
				customerNotification.setEmail(notify.getEmail());
				customerNotification.setDueDateTime(notify.getDueDateTime());
				customerNotification.setNotifyId(notify.getNotifyId());
				customerNotification.setPlaceHodersWithValues(placeHodersWithValues);

				if (notificationInfoMap.containsKey(notificationKey)) {
					customerNotificationList = notificationInfoMap.get(notificationKey);
				} else {
					customerNotificationList = new ArrayList<CustomerNotification>();
					notificationKey.setMessage(campaignsms.getMessage());
					notificationKey.setSubject(campaignsms.getSubject());
					notificationInfoMap.put(notificationKey, customerNotificationList);
				}
				customerNotificationList.add(customerNotification);
			}

		} else {
			logger.error("Notification list is empty");
		}
	}

	private void prepareEmailNotificationResponse(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, List<Notify> notifyList,
			Map<NotificationKey, List<CustomerNotification>> notificationInfoMap) throws TelAppointException {
		if (notifyList != null && notifyList.size() > 0) {
			CustomerNotification customerNotification = null;
			List<CustomerNotification> customerNotificationList = null;
			for (int i = 0; i < notifyList.size(); i++) {
				customerNotification = new CustomerNotification();
				Notify notify = notifyList.get(i);
				CampaignMessageEmail campaignEmail = notifyDAO.getCampaignMessageEmails(jdbcCustomTemplate, logger, notify.getCampaignId());

				// Above query need to changed - need to use cache.
				NotificationKey notificationKey = new NotificationKey();
				notificationKey.setCompaignId(notify.getCampaignId());

				// CustomerNotification bean populate
				Map<String, String> placeHodersWithValues = new HashMap<String, String>();
				String categories = notifyDAO.getPlaceHolderCategories(jdbcCustomTemplate, logger);

				if ("".equals(categories) == false) {
					String[] categoryArray = categories.split(",");
					String pkColumnId = "id";
					for (String category : categoryArray) {
						DynamicTemplatePlaceHolder dynamicTPH = new DynamicTemplatePlaceHolder();
						notifyDAO.getDynamicPlaceHolder(jdbcCustomTemplate, logger, category, dynamicTPH);
						if (category.equals("event_result")) {
							pkColumnId = "event_id";
						}
						notifyDAO.getPlaceHolderMap(jdbcCustomTemplate, logger, pkColumnId, "" + notify.getNotifyId(), dynamicTPH, placeHodersWithValues);
					}
				}
				customerNotification.setFirstName(notify.getFirstName());
				customerNotification.setLastName(notify.getLastName());
				customerNotification.setEmail(notify.getEmail());
				customerNotification.setDueDateTime(notify.getDueDateTime());
				customerNotification.setNotifyId(notify.getNotifyId());
				customerNotification.setPlaceHodersWithValues(placeHodersWithValues);

				if (notificationInfoMap.containsKey(notificationKey)) {
					customerNotificationList = notificationInfoMap.get(notificationKey);
				} else {
					customerNotificationList = new ArrayList<CustomerNotification>();
					notificationKey.setMessage(campaignEmail.getMessage());
					notificationKey.setSubject(campaignEmail.getSubject());
					notificationKey.setEnableHTML(campaignEmail.getEnableHtmlFlag());
					notificationInfoMap.put(notificationKey, customerNotificationList);
				}
				customerNotificationList.add(customerNotification);
			}
		} else {
			logger.error("Notification list is empty");
		}

	}

	public HashMap<String, String> getSMSAuthDetails(Logger logger, Integer clientId) throws TelAppointException {
		SMSConfig objSmsConfig = (SMSConfig) masterDAO.getSMSConfig(logger, clientId);
		HashMap<String, String> authDetails = new HashMap<String, String>();
		authDetails.put("ACCOUNT_SID", objSmsConfig.getAccountSID());
		authDetails.put("ACCOUNT_TOKEN", objSmsConfig.getAuthToken());
		authDetails.put("PHONE_NUMBER", objSmsConfig.getSmsPhone());
		return authDetails;
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

	public void removeDuplicateResv(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, List<Notify> notifyList) throws TelAppointException {
		List<Notify> updateNotifyList = new ArrayList<Notify>();

		// remove duplicate appointment for the same family & same date but
		// keep the earlier appt.
		for (int i = 0; i < notifyList.size(); i++) {
			Notify notify1 = (Notify) notifyList.get(i);
			for (int j = i + 1; j < notifyList.size(); j++) {
				Notify notify2 = (Notify) notifyList.get(j);
				String date1 = (notify1.getDueDateTime().toString()).substring(0, 10);
				String date2 = (notify2.getDueDateTime().toString()).substring(0, 10);

				// if appt date and last name and any one of the phone
				// number matches, then cancel the earlier appointment
				if ((date2).equals(date1)
						&& notify2.getEventId() == notify1.getEventId()
						&& (notify2.getLastName()).equals(notify1.getLastName())
						&& (CoreUtils.isStringEqual(notify2.getHomePhone(), notify1.getHomePhone()) || CoreUtils.isStringEqual(notify2.getWorkPhone(), notify1.getWorkPhone()) || CoreUtils
								.isStringEqual(notify2.getCellPhone(), notify1.getCellPhone()))) {
					updateNotifyList.add(notify2);

					logger.info("Removed duplicate resv :" + notify2.getNotifyId() + "," + date2 + "," + notify2.getLastName() + "," + notify2.getHomePhone() + ","
							+ notify2.getWorkPhone() + "," + notify2.getCellPhone());
					System.out.println("Removed duplicate resv :" + notify2.getNotifyId() + "," + date2 + "," + notify2.getLastName() + "," + notify2.getHomePhone() + ","
							+ notify2.getWorkPhone() + "," + notify2.getCellPhone());
				}
			}
		}
		notifyDAO.updateNotifyStatus(jdbcCustomTemplate, logger, updateNotifyList, NotifyStatusConstants.NOTIFY_STATUS_SUSPENDED.getNotifyStatus());

	}

	public String getNotifyEndDate(DialerSetting dialerSetting, String timeZone) throws TelAppointException {
		int minDays = dialerSetting.getDaysBeforeStartCalling();
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
		for (int i = 0; i < minDays; i++) {
			cal.add(Calendar.DATE, 1);
		}
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		ThreadLocal<DateFormat> format = DateUtils.getSimpleDateFormat(CommonDateContants.DATETIME_FORMAT_YYYYMMDDHHMMSS_CAP.getValue());
		return format.get().format(cal.getTime());
	}

	public ResponseEntity<ResponseModel> handleException(Logger logger, TelAppointException tae) {
		return commonComponent.handleException(logger, tae);
	}

	@Override
	public Logger getLogger(String clientCode, String logLevel) throws TelAppointException {
		return commonComponent.getLogger(clientCode, logLevel);
	}
}
