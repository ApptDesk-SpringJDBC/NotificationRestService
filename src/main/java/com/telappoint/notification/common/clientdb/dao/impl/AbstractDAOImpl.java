package com.telappoint.notification.common.clientdb.dao.impl;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.telappoint.notification.common.constants.ErrorConstants;
import com.telappoint.notification.common.constants.PropertiesConstants;
import com.telappoint.notification.common.model.Campaign;
import com.telappoint.notification.common.model.CampaignResponse;
import com.telappoint.notification.common.model.ClientDeploymentConfig;
import com.telappoint.notification.common.model.JdbcCustomTemplate;
import com.telappoint.notification.common.utils.CoreUtils;
import com.telappoint.notification.common.utils.PropertyUtils;
import com.telappoint.notification.handlers.exception.TelAppointException;
import com.telappoint.notification.model.Notify;

/**
 * 
 * @author Balaji N
 *
 */
public abstract class AbstractDAOImpl {

	public int getMaxAttemptId(JdbcCustomTemplate jdbcCustomTemplate, Logger customLogger, long notifyId) {
		String sql = "select max(nps.attempt_id) from notify_phone_status nps where nps.notify_id= ?";
		Integer attemptId = jdbcCustomTemplate.getJdbcTemplate().queryForInt(sql, new Object[] { notifyId });
		if (null == attemptId)
			return 0;
		return attemptId;
	}

	public void getCampaigns(JdbcCustomTemplate jdbcCustomTemplate, Logger customLogger, String langCode, String device, final boolean isFullData,
			final CampaignResponse campaignResponse) throws Exception {
		List<Campaign> campaignList = getCampaigns(jdbcCustomTemplate, customLogger, langCode, device, isFullData);
		campaignResponse.setCampaignList(campaignList);
	}

	public List<Campaign> getCampaigns(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, String langCode, String device, final boolean isFullData) throws TelAppointException {
		StringBuilder sql = new StringBuilder("select id,name, notify_by_phone,notify_by_phone_confirm,notify_by_sms,");
		sql.append("notify_by_sms_confirm,notify_by_email,notify_by_email_confirm, notify_by_push_notification, active from campaign where id > 0 and delete_flag='N'");

		final List<Campaign> campaignList = new ArrayList<Campaign>();
		return jdbcCustomTemplate.getJdbcTemplate().query(sql.toString(), new ResultSetExtractor<List<Campaign>>() {

			@Override
			public List<Campaign> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Campaign campaign = null;
				while (rs.next()) {
					campaign = new Campaign();
					campaign.setCampaignId(rs.getLong("id"));
					campaign.setCampaignName(rs.getString("name"));
					if (isFullData) {
						campaign.setNotifyByEmail(rs.getString("notify_by_email"));
						campaign.setNotifyBySMS(rs.getString("notify_by_sms"));
						campaign.setNotifyByPhone(rs.getString("notify_by_phone"));
						campaign.setNotifyByEmailConfirm(rs.getString("notify_by_email_confirm"));
						campaign.setNotifyBySMSConfirm(rs.getString("notify_by_sms_confirm"));
						campaign.setNotifyByPhoneConfirm(rs.getString("notify_by_phone_confirm"));
						campaign.setNotifyByPushNotification(rs.getString("notify_by_push_notification"));
						campaign.setActive(rs.getString("active"));
					} else {
						campaign.setActive(null);
					}
					campaignList.add(campaign);
				}
				return campaignList;
			}
		});

	}

	public boolean isValidToken(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, String clientCode, String device, String token, ClientDeploymentConfig cdConfig)
			throws TelAppointException {
		StringBuilder sql = new StringBuilder();
		sql.append("select count(1) from tokens tn where 1=1 ");
		sql.append(" and tn.token =").append("'").append(token).append("'");
		sql.append(" and expiry_stamp > ").append("CONVERT_TZ(now(),'US/Central','").append(cdConfig.getTimeZone()).append("')");
		sql.append(" and device=").append("'").append(device).append("'");
		sql.append(" and client_code=").append("'").append(clientCode).append("'");
		logger.debug("SQL for isValidToken: " + sql.toString());
		int count = jdbcCustomTemplate.getJdbcTemplate().queryForInt(sql.toString());
		boolean isValid = false;
		if (count > 0) {
			extendExpiryTimestamp(jdbcCustomTemplate, logger, clientCode, token, device, cdConfig);
			isValid = true;
		}
		return isValid;
	}

	public boolean isValidToken(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, String device, String token, ClientDeploymentConfig cdConfig,
			String customerId) throws TelAppointException {
		 String clientCode = jdbcCustomTemplate.getClientCode();
		StringBuilder sql = new StringBuilder();
		sql.append("select count(1) from tokens tn where 1=1 ");
		sql.append(" and tn.token =").append("'").append(token).append("'");
		sql.append(" and expiry_stamp > ").append("CONVERT_TZ(now(),'US/Central','").append(cdConfig.getTimeZone()).append("')");
		sql.append(" and device=").append("'").append(device).append("'");
		sql.append(" and client_code=").append("'").append(clientCode).append("'");
		sql.append(" and customer_id=").append(customerId);
		logger.debug("SQL for isValidToken: " + sql.toString());
		int count = jdbcCustomTemplate.getJdbcTemplate().queryForInt(sql.toString());
		boolean isValid = false;
		if (count > 0) {
			extendExpiryTimestamp(jdbcCustomTemplate, logger, clientCode, token, device, cdConfig);
			isValid = true;
		}
		return isValid;
	}

	private void extendExpiryTimestamp(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, String clientCode, String token, String device, ClientDeploymentConfig cdConfig)
			throws TelAppointException {
		try {
			String extendexpiryMinStr = PropertyUtils.getValueFromProperties("TOKEN_EXPIRY_IN_MINS", PropertiesConstants.NOTIFY_SERVICE_REST_WS_PROP.getPropertyFileName());
			if (extendexpiryMinStr == null) {
				extendexpiryMinStr = "15";
			}
			extendexpiryMinStr = "00:" + extendexpiryMinStr + ":00";
			StringBuilder sql = new StringBuilder("update tokens set expiry_stamp= CONVERT_TZ(ADDTIME(now(),'");
			sql.append(extendexpiryMinStr).append("'),'US/Central','");
			sql.append(cdConfig.getTimeZone()).append("') where  token=? and client_code=? and device=?");
			logger.debug("Extended expiry query:" + sql.toString());
			jdbcCustomTemplate.getJdbcTemplate().update(sql.toString(), new Object[] { token, clientCode, device });
		} catch (IOException e) {
			throw new TelAppointException(ErrorConstants.ERROR_1016.getErrorCode(), ErrorConstants.ERROR_1016.getUserErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
					"Expiry time not extended.", null);
		}
	}

	public String saveAndGetToken(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, long notifyId, String device, ClientDeploymentConfig cdConfig, String param2)
			throws TelAppointException {
		String clientCode = jdbcCustomTemplate.getClientCode();
		try {
			String expiryMinStr = PropertyUtils.getValueFromProperties("tokenExpiryInMin", PropertiesConstants.NOTIFY_SERVICE_REST_WS_PROP.getPropertyFileName());
			int expiryMin = 300;
			if(expiryMinStr != null) {
				expiryMin = Integer.parseInt(expiryMinStr);
			}
			 
			String token = CoreUtils.getToken(clientCode, device);
			StringBuilder sql = new StringBuilder("insert into tokens (notify_id, client_code, expiry_stamp, token) values (?,?,CONVERT_TZ(ADDTIME(now(),");
			sql.append("SEC_TO_TIME(1*" + expiryMin + "))").append(",'US/Central','").append(cdConfig.getTimeZone()).append("')");
			sql.append(",?)");
			logger.debug("saveAndGetToken SQL: " + sql.toString());
			int count = jdbcCustomTemplate.getJdbcTemplate().update(sql.toString(), new Object[] { notifyId, clientCode, token });
			if (count > 0) {
				return token;
			} else {
				throw new TelAppointException(ErrorConstants.ERROR_1016.getErrorCode(), ErrorConstants.ERROR_1016.getUserErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null,
						null);
			}
		} catch (IOException ie) {
			throw new TelAppointException(ErrorConstants.ERROR_1016.getErrorCode(), ErrorConstants.ERROR_1016.getUserErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null, null);
		}
	}
	
	public Notify getNotifyById(JdbcCustomTemplate jdbcCustomTemplate, long notifyId) throws TelAppointException {
		String sql = "select id,due_date_time,campaign_id from notify where id=?";
		return jdbcCustomTemplate.getJdbcTemplate().query(sql, new Object[] { notifyId }, new ResultSetExtractor<Notify>() {
			@Override
			public Notify extractData(ResultSet rs) throws SQLException, DataAccessException {
				Notify notify = null;
				if (rs.next()) {
					notify = new Notify();
					notify.setNotifyId(rs.getLong("id"));
					notify.setDueDateTime(rs.getString("due_date_time"));
					notify.setCampaignId(rs.getLong("campaign_id"));
				}
				return notify;
			}
		});
	}

	
	public boolean canUpdateNotifyStatus(JdbcCustomTemplate jdbcCustomTemplate, long notifyId) throws TelAppointException {
		com.telappoint.notification.model.Notify notify = getNotifyById(jdbcCustomTemplate, notifyId);

		String notifyByEmail = String.valueOf(notify.getNotifyByEmail());
		String notifyBySMS = String.valueOf(notify.getNotifyBySMS());
		String notifyByPhone = String.valueOf(notify.getNotifyByPhone());

		int notifyEmailStatus = notify.getNotifyEmailStatus();
		int notifySMSStatus = notify.getNotifySMSstatus();
		int notifyPhoneStatus = notify.getNotifyPhoneStatus();

		boolean emailEnable = "Y".equals(notifyByEmail) ? true : false;
		boolean smsEnable = "Y".equals(notifyBySMS) ? true : false;
		boolean phoneEnable = "Y".equals(notifyByPhone) ? true : false;

		boolean isSentEmail = (emailEnable && notifyEmailStatus > 2) ? true : false;
		boolean isSentSMS = (smsEnable && notifySMSStatus > 2) ? true : false;
		boolean isSentPhone = (phoneEnable && notifyPhoneStatus > 2) ? true : false;
		boolean finalStatus = (emailEnable || smsEnable || phoneEnable) ? true : false;
		if (emailEnable) {
			if (isSentEmail == false)
				finalStatus = false;
		}
		if (smsEnable) {
			if (isSentSMS == false)
				finalStatus = false;
		}

		if (phoneEnable) {
			if (isSentPhone == false)
				finalStatus = false;
		}
		return finalStatus;
	}
}
