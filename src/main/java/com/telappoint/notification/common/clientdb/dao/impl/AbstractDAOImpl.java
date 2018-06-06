package com.telappoint.notification.common.clientdb.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.telappoint.notification.common.constants.ErrorConstants;
import com.telappoint.notification.common.model.Campaign;
import com.telappoint.notification.common.model.CampaignResponse;
import com.telappoint.notification.common.model.JdbcCustomTemplate;
import com.telappoint.notification.handlers.exception.TelAppointException;


/**
 * 
 * @author Balaji N
 *
 */
public abstract class AbstractDAOImpl {

	public int getMaxAttemptId(JdbcCustomTemplate jdbcCustomTemplate, Logger customLogger, long notifyId) {
		String sql = "select max(nps.attempt_id) from notify_phone_status nps where nps.notify_id= ?";
		Integer attemptId = jdbcCustomTemplate.getJdbcTemplate().queryForInt(sql, new Object[]{notifyId});
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
		sql.append("notify_by_sms_confirm,notify_by_email,notify_by_email_confirm, notify_by_push_notification, active from campaign where id > 0");
		
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
}
