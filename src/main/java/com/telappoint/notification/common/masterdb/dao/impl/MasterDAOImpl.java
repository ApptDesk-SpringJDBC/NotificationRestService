package com.telappoint.notification.common.masterdb.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import com.telappoint.notification.common.constants.ErrorConstants;
import com.telappoint.notification.common.masterdb.dao.MasterDAO;
import com.telappoint.notification.common.model.Client;
import com.telappoint.notification.common.model.ClientDeploymentConfig;
import com.telappoint.notification.handlers.exception.TelAppointException;
import com.telappoint.notification.model.SMSConfig;

/**
 * 
 * @author Balaji N
 *
 */
@Repository
public class MasterDAOImpl implements MasterDAO {

	@Autowired
	private JdbcTemplate masterJdbcTemplate;

	public MasterDAOImpl(JdbcTemplate jdbcTemplate) {
		this.masterJdbcTemplate = jdbcTemplate;
	}

	public MasterDAOImpl() {
	}

	@Override
	public void getClients(final String key, final Map<String, Client> clientCacheMap) throws TelAppointException {
		String query = "select * from client c where delete_flag='N'";
		try {
			masterJdbcTemplate.query(query.toString(), new ResultSetExtractor<Map<String, Client>>() {
				@Override
				public Map<String, Client> extractData(ResultSet rs) throws SQLException, DataAccessException {
					Client client = null;
					String clientCode = null;
					while (rs.next()) {
						clientCode = rs.getString("client_code");
						client = new Client();
						client.setClientId(rs.getInt("id"));
						client.setClientCode(clientCode);
						client.setClientName(rs.getString("client_name"));
						client.setWebsite(rs.getString("website"));
						client.setContactEmail(rs.getString("contact_email"));
						client.setFax(rs.getString("fax"));
						client.setAddress(rs.getString("address"));
						client.setAddress2(rs.getString("address2"));
						client.setCity(rs.getString("city"));
						client.setState(rs.getString("state"));
						client.setZip(rs.getString("zip"));
						client.setCountry(rs.getString("country"));
						client.setDbName(rs.getString("db_name"));
						client.setDbServer(rs.getString("db_server"));
						client.setCacheEnabled(rs.getString("cache_enabled"));
						client.setApptLink(rs.getString("appt_link"));
						client.setDirectAccessNumber(rs.getString("direct_access_number"));
						clientCacheMap.put(key + "|" + clientCode, client);
					}
					return clientCacheMap;
				}
			});
		} catch (DataAccessException dae) {
			throw new TelAppointException(ErrorConstants.ERROR_1000.getErrorCode(), ErrorConstants.ERROR_1000.getUserErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
					dae.toString(), null);
		}
	}

	@Override
	public void getClientDeploymentConfig(final String key, final String clientCode, int clientId, final Map<String, Object> cacheObjectMap) throws TelAppointException {
		String query = "select * from client_deployment_config c where client_id = ?";

		try {
			masterJdbcTemplate.query(query.toString(), new Object[] { clientId }, new ResultSetExtractor<Map<String, Object>>() {
				@Override
				public Map<String, Object> extractData(ResultSet rs) throws SQLException, DataAccessException {
					ClientDeploymentConfig clientDeploymentConfig = null;
					if (rs.next()) {
						clientDeploymentConfig = new ClientDeploymentConfig();
						clientDeploymentConfig.setTimeZone(rs.getString("time_zone"));
						clientDeploymentConfig.setDateFormat(rs.getString("date_format"));
						clientDeploymentConfig.setTimeFormat(rs.getString("time_format"));
						clientDeploymentConfig.setDateyyyyFormat(rs.getString("date_yyyy_format"));
						clientDeploymentConfig.setFullDateFormat(rs.getString("full_date_format"));
						clientDeploymentConfig.setFullDatetimeFormat(rs.getString("full_datetime_format"));
						clientDeploymentConfig.setFullTextualdayFormat(rs.getString("full_textualday_format"));
						clientDeploymentConfig.setPhoneFormat(rs.getString("phone_format"));
						clientDeploymentConfig.setPopupCalendardateFormat(rs.getString("popup_calendardate_format"));
						clientDeploymentConfig.setLeadTimeInSeconds(rs.getInt("notify_phone_lead_time"));
						clientDeploymentConfig.setLagTimeInSeconds(rs.getInt("notify_phone_lag_time"));

						cacheObjectMap.put(key + "|" + clientCode, clientDeploymentConfig);
					}
					return cacheObjectMap;
				}
			});
		} catch (DataAccessException dae) {
			throw new TelAppointException(ErrorConstants.ERROR_1001.getErrorCode(), ErrorConstants.ERROR_1001.getUserErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
					dae.toString(), null);
		}
	}

	@Override
	public SMSConfig getSMSConfig(Logger logger, Integer clientId) throws TelAppointException {
		String query = "select * from sms_config c where client_id=?";
		try {
			return masterJdbcTemplate.query(query.toString(), new Object[] { clientId }, new ResultSetExtractor<SMSConfig>() {
				@Override
				public SMSConfig extractData(ResultSet rs) throws SQLException, DataAccessException {
					SMSConfig smsConfig = null;
					if (rs.next()) {
						smsConfig = new SMSConfig();
						smsConfig.setAccountSID(rs.getString("account_sid"));
						smsConfig.setAuthToken(rs.getString("auth_token"));
						smsConfig.setSmsPhone(rs.getString("sms_phone"));
						smsConfig.setSmsConfigId(rs.getInt("id"));
					}
					return smsConfig;
				}
			});
		} catch (DataAccessException dae) {
			throw new TelAppointException(ErrorConstants.ERROR_1002.getErrorCode(), ErrorConstants.ERROR_1002.getUserErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
					dae.toString(), null);
		}
	}
}
