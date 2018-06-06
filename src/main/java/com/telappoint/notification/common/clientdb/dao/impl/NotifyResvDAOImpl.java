package com.telappoint.notification.common.clientdb.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.telappoint.notification.common.clientdb.dao.NotifyResvDAO;
import com.telappoint.notification.common.constants.CommonResvDeskConstants;
import com.telappoint.notification.common.constants.ErrorConstants;
import com.telappoint.notification.common.model.BaseRequest;
import com.telappoint.notification.common.model.ClientDeploymentConfig;
import com.telappoint.notification.common.model.ErrorLog;
import com.telappoint.notification.common.model.JdbcCustomTemplate;
import com.telappoint.notification.common.model.NotifyRequest;
import com.telappoint.notification.common.utils.CoreUtils;
import com.telappoint.notification.constants.NotifyStatusConstants;
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
 * @author Balaji N
 *
 */
@Repository
public class NotifyResvDAOImpl extends AbstractDAOImpl implements NotifyResvDAO {

	@Override
	public boolean isLocked(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, String device, int timeInterval, ClientDeploymentConfig cdConfig) throws TelAppointException {
		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) from dialer_lock dl where 1=1");
		sql.append(" and (dl.locked = 'N' and (ADDTIME(dl.start_time, SEC_TO_TIME(1*" + timeInterval + ")) > ");
		sql.append("CONVERT_TZ(now(),'US/Central','").append(cdConfig.getTimeZone());
		sql.append("')) or (dl.locked = 'Y' and ADDTIME(dl.end_time, '00:10') > ");
		sql.append("CONVERT_TZ(now(),'US/Central','").append(cdConfig.getTimeZone()).append("')))");
		sql.append(" and dl.device_type=?");
		logger.debug("sql: " + sql.toString());
		int count = jdbcCustomTemplate.getJdbcTemplate().queryForInt(sql.toString(), new Object[] { device });
		if (count > 0) {
			// can't dial
			return false;
		}
		// system can dail.
		return true;
	}

	@Override
	public void updateDialerLockStartTimer(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, String device, String timeZone) throws TelAppointException {
		StringBuilder sql = new StringBuilder();
		sql.append("update dialer_lock dl set dl.start_time=");
		sql.append("CONVERT_TZ(now(),'US/Central','").append(timeZone).append("')");
		sql.append(" , dl.end_time=");
		sql.append("CONVERT_TZ(now(),'US/Central','").append(timeZone).append("')");
		sql.append(" where dl.device_type=?");
		System.out.println("Updating start time and end time ");
		jdbcCustomTemplate.getJdbcTemplate().update(sql.toString(), new Object[] { device });
	}

	@Override
	public void updateDialerLocked(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, String device, String timeZone) throws TelAppointException {
		StringBuilder sql = new StringBuilder();
		sql.append("update dialer_lock dl set dl.locked='Y'");
		sql.append(" where dl.device_type=?");
		jdbcCustomTemplate.getJdbcTemplate().update(sql.toString(), new Object[] { device });
		logger.info("updated locked = Y");

	}

	@Override
	public void updateDialerUnLocked(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, String device, String timeZone) throws TelAppointException {
		StringBuilder sql = new StringBuilder();
		sql.append("update dialer_lock dl set dl.locked='N'");
		sql.append(" where dl.device_type=?");
		jdbcCustomTemplate.getJdbcTemplate().update(sql.toString(), new Object[] { device });
		logger.info("updated locked = N");
	}

	@Override
	public void updateDialerLockEndTime(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, String device, String timeZone) throws TelAppointException {
		StringBuilder sql = new StringBuilder();
		sql.append("update dialer_lock dl set ");
		sql.append(" dl.end_time=CONVERT_TZ(now(),'US/Central','").append(timeZone).append("')");
		sql.append(" where device_type=?");
		jdbcCustomTemplate.getJdbcTemplate().update(sql.toString(), new Object[] { device });
	}

	@Override
	public DialerSetting getDailerSettings(JdbcCustomTemplate jdbcCustomTemplate, Long campaignId) throws TelAppointException {
		StringBuilder sql = new StringBuilder("select campaign_id,call_from_1, call_to_1");
		sql.append(",call_from_2, call_to_2,call_mon,call_tue,call_wed,call_thu, call_fri,call_sat, days_before_start_calling, ");
		sql.append("hours_stop_calling,tot_max_attempts, max_attempts_per_day");
		sql.append(" from dialer_settings where campaign_id=?");
		try {
			return jdbcCustomTemplate.getJdbcTemplate().query(sql.toString(), new Object[] { campaignId }, new ResultSetExtractor<DialerSetting>() {
				@Override
				public DialerSetting extractData(ResultSet rs) throws SQLException, DataAccessException {
					DialerSetting dialerSetting = null;
					if (rs.next()) {
						dialerSetting = new DialerSetting();
						dialerSetting.setCampaignId(rs.getLong("campaign_id"));
						dialerSetting.setCallFrom1(rs.getString("call_from_1"));
						dialerSetting.setCallTo1(rs.getString("call_to_1"));
						dialerSetting.setCallFrom2(rs.getString("call_from_2"));
						dialerSetting.setCallTo2(rs.getString("call_to_2"));
						dialerSetting.setCallMon(rs.getString("call_mon"));
						dialerSetting.setCallTue(rs.getString("call_tue"));
						dialerSetting.setCallWed(rs.getString("call_wed"));
						dialerSetting.setCallThu(rs.getString("call_thu"));
						dialerSetting.setCallFri(rs.getString("call_fri"));
						dialerSetting.setCallSat(rs.getString("call_sat"));
						dialerSetting.setHoursStopCalling(rs.getInt("hours_stop_calling"));
						dialerSetting.setDaysBeforeStartCalling(rs.getInt("days_before_start_calling"));
						dialerSetting.setTotMaxAttempts(rs.getInt("tot_max_attempts"));
						dialerSetting.setMaxAttemptsPerDay(rs.getInt("max_attempts_per_day"));
					}
					return dialerSetting;
				}
			});
		} catch (DataAccessException dae) {
			throw new TelAppointException(ErrorConstants.ERROR_1003.getErrorCode(), ErrorConstants.ERROR_1003.getUserErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
					dae.toString(), "CampaignId: " + campaignId);
		}
	}

	@Override
	public void getNotifyList(final JdbcCustomTemplate jdbcCustomTemplate, final Logger logger, DialerSetting dialerSetting, String timeZone, String endDate, final String device,
			final List<Notify> notifyList, final boolean updateInProgress) throws TelAppointException {

		StringBuilder sql = new StringBuilder();
		sql.append("select * from notify ntf where 1=1");
		sql.append(" and ntf.due_date_time >='" + CoreUtils.getNotifyStartDate(dialerSetting.getHoursStopCalling(), timeZone));
		sql.append("' and date(due_date_time) <= '");
		sql.append(endDate);
		sql.append("' and ntf.do_not_notify = 'N' ");
		sql.append(" and ntf.delete_flag = 'N' ");

		if (CommonResvDeskConstants.EMAIL_REMINDER.getValue().equals(device)) {
			sql.append(" and ntf.notify_by_email = 'Y'");
			sql.append(" and ntf.notify_email_status <= 2");
		} else if (CommonResvDeskConstants.PHONE_REMINDER.getValue().equals(device)) {
			sql.append(" and ntf.notify_by_phone = 'Y'");
			sql.append(" and ntf.notify_phone_status <= 2");
		} else if (CommonResvDeskConstants.SMS_REMINDER.getValue().equals(device)) {
			sql.append(" and ntf.notify_by_sms = 'Y'");
			sql.append(" and ntf.notify_sms_status <= 2");
		}
		sql.append(" and ntf.notify_status in (1,2)");
		sql.append(" and ntf.campaign_id =" + dialerSetting.getCampaignId());
		sql.append(" order by due_date_time ");

		try {
			jdbcCustomTemplate.getJdbcTemplate().query(sql.toString(), new ResultSetExtractor<Long>() {
				public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
					Notify notify = null;
					while (rs.next()) {
						notify = new Notify();
						notify.setCampaignId(rs.getLong("campaign_id"));
						notify.setNotifyId(rs.getLong("id"));
						notify.setFirstName(rs.getString("first_name"));
						notify.setLastName(rs.getString("last_name"));
						notify.setEmail(rs.getString("email"));
						notify.setDueDateTime(rs.getString("due_date_time"));
						notify.setNotifyEmailStatus(rs.getInt("notify_email_status"));
						notify.setNotifySMSstatus(rs.getInt("notify_sms_status"));
						notify.setNotifyPhoneStatus(rs.getInt("notify_phone_status"));
						notify.setNotifyStatus(rs.getInt("notify_status"));
						notify.setHomePhone(rs.getString("home_phone"));
						notify.setCellPhonel(rs.getString("cell_phone"));
						notify.setWorkPhone(rs.getString("work_phone"));
						notifyList.add(notify);
					}
					if (updateInProgress && notifyList.size() > 0) {
						updateNotificationsByBatch(jdbcCustomTemplate, logger, notifyList, device);
					}
					return null;
				}
			});
		} catch (DataAccessException dae) {
			throw new TelAppointException(ErrorConstants.ERROR_1004.getErrorCode(), ErrorConstants.ERROR_1004.getUserErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
					dae.toString(), null);
		}
		logger.info("Notify Result:" + sql.toString());
	}

	public void updateNotificationsByBatch(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, final List<Notify> notifyList, final String device) throws DataAccessException {
		final StringBuilder sql = new StringBuilder("update notify set ");

		MapSqlParameterSource mapSQLParameterSource = new MapSqlParameterSource();
		if (CommonResvDeskConstants.EMAIL_REMINDER.getValue().equals(device)) {
			sql.append(" notify_email_status=:EmailStatus");
			mapSQLParameterSource.addValue("EmailStatus", NotifyStatusConstants.NOTIFY_STATUS_IN_PROGRESS.getNotifyStatus());
		} else if (CommonResvDeskConstants.PHONE_REMINDER.getValue().equals(device)) {
			sql.append(" notify_phone_status=:PhoneStatus");
			mapSQLParameterSource.addValue("PhoneStatus", NotifyStatusConstants.NOTIFY_STATUS_IN_PROGRESS.getNotifyStatus());
		} else if (CommonResvDeskConstants.SMS_REMINDER.getValue().equals(device)) {
			sql.append(" notify_sms_status=:SMSStatus");
			mapSQLParameterSource.addValue("SMSStatus", NotifyStatusConstants.NOTIFY_STATUS_IN_PROGRESS.getNotifyStatus());
		}
		sql.append(",notify_status=:NotifyStatus");
		sql.append(" where id in (:NotifyId)");
		mapSQLParameterSource.addValue("NotifyStatus", NotifyStatusConstants.NOTIFY_STATUS_IN_PROGRESS.getNotifyStatus());

		Set<Long> list = new HashSet<Long>();
		for (Notify notify : notifyList) {
			list.add(notify.getNotifyId());
		}
		mapSQLParameterSource.addValue("NotifyId", list);

		try {
			jdbcCustomTemplate.getNameParameterJdbcTemplate().update(sql.toString(), mapSQLParameterSource);
		} catch (DataAccessException dae) {
			throw dae;
		}
	}

	@Override
	public void updateNotifyStatus(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, final List<Notify> notifyList, final int status) throws TelAppointException {
		final StringBuilder sql = new StringBuilder("update notify set notify_status=?");
		sql.append(" where id=?");

		try {
			jdbcCustomTemplate.getJdbcTemplate().batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
				final AtomicInteger index = new AtomicInteger(1);

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					Notify notify = notifyList.get(i);
					ps.setInt(index.getAndIncrement(), status);
					ps.setLong(index.getAndIncrement(), notify.getNotifyId());
				}

				@Override
				public int getBatchSize() {
					return notifyList.size();
				}
			});
		} catch (DataAccessException dae) {
			throw new TelAppointException(ErrorConstants.ERROR_1005.getErrorCode(), ErrorConstants.ERROR_1005.getUserErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
					dae.toString(), null);
		}
	}

	@Override
	public CampaignMessageEmail getCampaignMessageEmails(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, Long campaignId) throws TelAppointException {
		StringBuilder sql = new StringBuilder();
		sql.append("select * from campaign_message_email cme Where 1=1");
		sql.append(" and cme.campaign_id=?");
		try {
			return jdbcCustomTemplate.getJdbcTemplate().query(sql.toString(), new Object[] { campaignId }, new ResultSetExtractor<CampaignMessageEmail>() {
				@Override
				public CampaignMessageEmail extractData(ResultSet rs) throws SQLException, DataAccessException {
					CampaignMessageEmail campaignMessageEmail = null;
					while (rs.next()) {
						campaignMessageEmail = new CampaignMessageEmail();
						campaignMessageEmail.setCampaignMessageEmailId(rs.getLong("id"));
						campaignMessageEmail.setCampaignId(rs.getLong("campaign_id"));
						campaignMessageEmail.setLang(rs.getString("lang"));
						campaignMessageEmail.setSubject(rs.getString("subject"));
						campaignMessageEmail.setMessage(rs.getString("message"));
						campaignMessageEmail.setEnableHtmlFlag(rs.getString("enable_html_flag"));
					}
					return campaignMessageEmail;
				}
			});
		} catch (DataAccessException dae) {
			throw new TelAppointException(ErrorConstants.ERROR_1006.getErrorCode(), ErrorConstants.ERROR_1006.getUserErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
					dae.toString(), "CampaignId:" + campaignId);
		}
	}

	@Override
	public void getNotifyWithCustomerIdGreaterThanZero(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, NotifyRequest notifyRequest, final List<Notify> notifyList)
			throws TelAppointException {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append(" select group_concat(distinct customer_id), first_name from notify notify where 1=1 and customer_id>0");
			sql.append(" and notify.do_not_notify = 'N' ");
			sql.append(" and notify.delete_flag = 'N' ");

			boolean showEditNotify = false;
			StringBuilder subSql = new StringBuilder();
			if ("Y".equals(notifyRequest.getNotifyByPhone())) {
				showEditNotify = true;
				subSql.append(" notify.notify_by_phone = 'Y'");
			}
			if ("Y".equals(notifyRequest.getNotifyByEmail()) && !showEditNotify) {
				showEditNotify = true;
				subSql.append(" notify.notify_by_email = 'Y'");
			} else if ("Y".equals(notifyRequest.getNotifyByEmail())) {
				subSql.append(" or notify.notify_by_email = 'Y'");
			}
			if ("Y".equals(notifyRequest.getNotifyBySMS()) && !showEditNotify) {
				showEditNotify = true;
				subSql.append(" notify.notify_by_sms = 'Y'");
			} else if ("Y".equals(notifyRequest.getNotifyBySMS())) {
				subSql.append(" or notify.notify_by_sms = 'Y'");
			}
			if (subSql.length() > 0) {
				sql.append(" and ( ");
				sql.append(subSql);
				sql.append(" ) ");
			}

			jdbcCustomTemplate.getJdbcTemplate().query(sql.toString(), new ResultSetExtractor<List<Notify>>() {
				@Override
				public List<Notify> extractData(ResultSet rs) throws SQLException, DataAccessException {
					Notify notify = null;
					while (rs.next()) {
						notify = new Notify();
						notify.setCampaignId(rs.getLong("campaign_id"));
						notify.setNotifyId(rs.getLong("id"));
						notify.setFirstName(rs.getString("first_name"));
						notify.setLastName(rs.getString("last_name"));
						notify.setEmail(rs.getString("email"));
						notify.setDueDateTime(rs.getString("due_date_time"));
						notify.setNotifyEmailStatus(rs.getInt("notify_email_status"));
						notify.setNotifySMSstatus(rs.getInt("notify_sms_status"));
						notify.setNotifyPhoneStatus(rs.getInt("notify_phone_status"));
						notify.setNotifyStatus(rs.getInt("notify_status"));
						notify.setHomePhone(rs.getString("home_phone"));
						notify.setCellPhonel(rs.getString("cell_phone"));
						notify.setWorkPhone(rs.getString("work_phone"));
						notifyList.add(notify);
					}
					return notifyList;
				}
			});
		} catch (DataAccessException dae) {
			throw new TelAppointException(ErrorConstants.ERROR_1007.getErrorCode(), ErrorConstants.ERROR_1007.getUserErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
					dae.toString(), null);
		}
		return;
	}

	@Override
	public CampaignMessagePhone getCampaignMessagePhone(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, Long campaignId) throws TelAppointException {
		StringBuilder sql = new StringBuilder();
		sql.append("select * from campaign_message_sms cme Where 1=1");
		sql.append(" and cme.campaign_id=?");
		try {
			return jdbcCustomTemplate.getJdbcTemplate().query(sql.toString(), new Object[] { campaignId }, new ResultSetExtractor<CampaignMessagePhone>() {
				@Override
				public CampaignMessagePhone extractData(ResultSet rs) throws SQLException, DataAccessException {
					CampaignMessagePhone campaignMessagePhone = null;
					while (rs.next()) {
						campaignMessagePhone = new CampaignMessagePhone();
						campaignMessagePhone.setCampaignMessagePhoneId(rs.getLong("id"));
						campaignMessagePhone.setCampaignId(rs.getLong("campaign_id"));
						campaignMessagePhone.setLang(rs.getString("lang"));
						campaignMessagePhone.setTtsOnly(rs.getString("tts_only"));
						campaignMessagePhone.setMessage(rs.getString("message"));
					}
					return campaignMessagePhone;
				}
			});
		} catch (DataAccessException dae) {
			throw new TelAppointException(ErrorConstants.ERROR_1008.getErrorCode(), ErrorConstants.ERROR_1008.getUserErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
					dae.toString(), null);
		}
	}

	@Override
	public void getPlaceHolderMap(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, String pkcolumnId, String pkvalue, DynamicTemplatePlaceHolder dynamicTPH,
			final Map<String, String> map) throws TelAppointException {
		String dynamicSelect = dynamicTPH.getDynamicSelect();
		String dynamicFrom = dynamicTPH.getDynamicFrom();
		final String dynamicAlias = dynamicTPH.getAliasName();
		final String dynamicType = dynamicTPH.getTypes();

		final String placeHolders = dynamicTPH.getDynamicPlaceHolder();
		StringBuilder sql = new StringBuilder();
		sql.append("select ").append(dynamicSelect);
		sql.append(" from ").append(dynamicFrom);
		sql.append(" where ").append(pkcolumnId).append("=").append(pkvalue);
		try {
			jdbcCustomTemplate.getJdbcTemplate().query(sql.toString(), new ResultSetExtractor<Long>() {
				@Override
				public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
					String alias[] = dynamicAlias.split(",");
					String types[] = dynamicType.split(",");
					String placeHolder[] = placeHolders.split(",");
					while (rs.next()) {
						try {
							for (int i = 0; i < alias.length; i++) {
								if ("int".equalsIgnoreCase(types[i])) {
									map.put(placeHolder[i], "" + rs.getInt(alias[i]));
								}

								if ("long".equalsIgnoreCase(types[i])) {
									map.put(placeHolder[i], "" + rs.getLong(alias[i]));
								}

								if ("varchar".equalsIgnoreCase(types[i])) {
									map.put(placeHolder[i], rs.getString(alias[i]));
								}
							}
						} catch (Exception e) {
							throw new SQLException("Exception in executeQueryDynamicQuery.");
						}
					}
					return null;
				}
			});

		} catch (DataAccessException dae) {
			throw new TelAppointException(ErrorConstants.ERROR_1009.getErrorCode(), ErrorConstants.ERROR_1009.getUserErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
					dae.toString(), null);
		}
	}

	@Override
	public void getDynamicPlaceHolder(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, String category, final DynamicTemplatePlaceHolder dynamicTPH)
			throws TelAppointException {
		StringBuilder sql = new StringBuilder();
		sql.append("select group_concat(distinct table_name order by id) as tableNames, ");
		sql.append("group_concat(concat(table_column,' as ',alias_name) order by id) as columnNames,");
		sql.append(" group_concat(alias_name order by id) as aliasName,");
		sql.append(" group_concat(`type` order by id) as type,");
		sql.append(" group_concat(`place_holder` order by id) as placeHolder");
		sql.append(" from dynamic_template_placeholder where category= ?");
		try {
			jdbcCustomTemplate.getJdbcTemplate().query(sql.toString(), new Object[] { category }, new ResultSetExtractor<DynamicTemplatePlaceHolder>() {
				@Override
				public DynamicTemplatePlaceHolder extractData(ResultSet rs) throws SQLException, DataAccessException {
					if (rs.next()) {
						dynamicTPH.setDynamicFrom(rs.getString("tableNames"));
						dynamicTPH.setDynamicSelect(rs.getString("columnNames"));
						dynamicTPH.setDynamicPlaceHolder(rs.getString("placeHolder"));
						dynamicTPH.setAliasName(rs.getString("aliasName"));
						dynamicTPH.setTypes(rs.getString("type"));
					}
					return dynamicTPH;
				}
			});
		} catch (DataAccessException dae) {
			throw new TelAppointException(ErrorConstants.ERROR_1010.getErrorCode(), ErrorConstants.ERROR_1010.getUserErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
					dae.toString(), null);
		}
	}

	@Override
	public String getPlaceHolderCategories(JdbcCustomTemplate jdbcCustomTemplate, Logger logger) throws TelAppointException {
		StringBuilder sql = new StringBuilder();
		sql.append("select group_concat(distinct category) as categoryStr from dynamic_template_placeholder");
		try {
			return jdbcCustomTemplate.getJdbcTemplate().query(sql.toString(), new ResultSetExtractor<String>() {
				@Override
				public String extractData(ResultSet rs) throws SQLException, DataAccessException {
					if (rs.next()) {
						return rs.getString("categoryStr");
					}
					return "";
				}
			});
		} catch (DataAccessException dae) {
			throw new TelAppointException(ErrorConstants.ERROR_1011.getErrorCode(), ErrorConstants.ERROR_1011.getUserErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
					dae.toString(), null);
		}

	}

	@Override
	public long getAttemptCountByToday(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, long notifyId, String startDateTime, String endDateTime) throws TelAppointException {
		StringBuilder sql = new StringBuilder("select count(*) as count from notify_phone_status nps where nps.notify_id=?");
		sql.append(" and nps.call_timestamp >='" + startDateTime);
		sql.append("' and nps.call_timestamp <= '" + endDateTime + "'");
		Integer attemptCountToday = jdbcCustomTemplate.getJdbcTemplate().queryForInt(sql.toString(), new Object[] { notifyId });
		if (null == attemptCountToday)
			return 0;

		return attemptCountToday;
	}

	@Override
	public Notify getNotifyById(JdbcCustomTemplate jdbcCustomTemplate, long notifyId) throws TelAppointException {
		String sql = "select id from notify where id=?";
		return jdbcCustomTemplate.getJdbcTemplate().query(sql, new Object[] { notifyId }, new ResultSetExtractor<Notify>() {
			@Override
			public Notify extractData(ResultSet rs) throws SQLException, DataAccessException {
				Notify notify = null;
				if (rs.next()) {
					notify = new Notify();
					notify.setNotifyId(rs.getLong("id"));
				}
				return notify;
			}
		});
	}

	@Override
	public void savePhoneRecords(JdbcCustomTemplate jdbcCustomTemplate, final List<OutboundPhoneLogs> outboundPhoneList) throws TelAppointException {
		StringBuilder outboundLogSQL = new StringBuilder();
		outboundLogSQL.append("insert into outbound_phone_logs(timestamp,call_id,phone,call_date, attempts,pickups,reason,duration, notify_id, result, cause)");
		outboundLogSQL.append(" values (now(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		DataSourceTransactionManager dsTransactionManager = jdbcCustomTemplate.getDataSourceTransactionManager();
		TransactionDefinition def = new DefaultTransactionDefinition();
		TransactionStatus status = dsTransactionManager.getTransaction(def);
		try {
			jdbcCustomTemplate.getJdbcTemplate().batchUpdate(outboundLogSQL.toString(), new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					OutboundPhoneLogs obpl = outboundPhoneList.get(i);
					ps.setLong(1, obpl.getCallId());
					ps.setString(2, obpl.getPhone());
					ps.setString(3, obpl.getCallDate());
					ps.setLong(4, obpl.getAttemptId());
					ps.setInt(5, obpl.getPickups());
					ps.setString(6, obpl.getReason());
					ps.setInt(7, obpl.getDuration());
					ps.setLong(8, obpl.getNotifyId());
					ps.setString(9, obpl.getResult());
					ps.setString(10, obpl.getCause());
				}

				@Override
				public int getBatchSize() {
					return outboundPhoneList.size();
				}
			});
			dsTransactionManager.commit(status);
		} catch (DataAccessException dae) {
			dsTransactionManager.rollback(status);
			throw new TelAppointException(ErrorConstants.ERROR_1012.getErrorCode(), ErrorConstants.ERROR_1012.getUserErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
					dae.toString(), null);
		}

	}

	@Override
	public FileUpload getFileUploadDetails(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, String fileType) throws TelAppointException {
		StringBuilder sql = new StringBuilder();
		sql.append("select file_type, max_file_size_kb,ignore_rows from file_upload fu where 1=1");
		sql.append(" and fu.file_type=?");
		return jdbcCustomTemplate.getJdbcTemplate().query(sql.toString(), new Object[] { fileType.toUpperCase() }, new ResultSetExtractor<FileUpload>() {
			FileUpload fileUpload = null;

			@Override
			public FileUpload extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					fileUpload = new FileUpload();
					fileUpload.setFileType(rs.getString("file_type"));
					fileUpload.setMaxFileSizeKB(rs.getLong("max_file_size_kb"));
					fileUpload.setIgnoreRows(rs.getInt("ignore_rows"));
				}
				return fileUpload;
			}
		});
	}

	@Override
	public void saveNotifyPhoneStatusRecords(JdbcCustomTemplate jdbcCustomTemplate, final List<NotifyPhoneStatus> notifyPhoneStatusList) throws TelAppointException {
		StringBuilder npsSQL = new StringBuilder();
		npsSQL.append("insert into notify_phone_status(notify_id,attempt_id,phone, call_status,call_timestamp,seconds)");
		npsSQL.append(" values (?, ?, ?, ?, ?, ?)");

		DataSourceTransactionManager dsTransactionManager = jdbcCustomTemplate.getDataSourceTransactionManager();
		TransactionDefinition def = new DefaultTransactionDefinition();
		TransactionStatus status = dsTransactionManager.getTransaction(def);
		try {
			jdbcCustomTemplate.getJdbcTemplate().batchUpdate(npsSQL.toString(), new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					NotifyPhoneStatus nps = notifyPhoneStatusList.get(i);
					ps.setLong(1, nps.getNotifyId());
					ps.setLong(2, nps.getAttemptId());
					ps.setString(3, nps.getPhone());
					ps.setInt(4, nps.getCallStatus());
					ps.setString(5, nps.getCallTimestamp());
					ps.setLong(6, nps.getSeconds());
				}

				@Override
				public int getBatchSize() {
					return notifyPhoneStatusList.size();
				}
			});
			dsTransactionManager.commit(status);
		} catch (DataAccessException dae) {
			dsTransactionManager.rollback(status);
			throw new TelAppointException(ErrorConstants.ERROR_1013.getErrorCode(), ErrorConstants.ERROR_1013.getUserErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
					dae.toString(), null);
		}
	}

	@Override
	public void updateNotifyPhoneStatusSeconds(JdbcCustomTemplate jdbcCustomTemplate, final List<NotifyPhoneStatus> updatePhoneList) throws TelAppointException {
		StringBuilder npsSQL = new StringBuilder();
		npsSQL.append("update notify_phone_status set seconds=? where notify_id=? and attempt_id=?");
		DataSourceTransactionManager dsTransactionManager = jdbcCustomTemplate.getDataSourceTransactionManager();
		TransactionDefinition def = new DefaultTransactionDefinition();
		TransactionStatus status = dsTransactionManager.getTransaction(def);
		try {
			jdbcCustomTemplate.getJdbcTemplate().batchUpdate(npsSQL.toString(), new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					NotifyPhoneStatus nps = updatePhoneList.get(i);
					ps.setLong(1, nps.getSeconds());
					ps.setLong(2, nps.getNotifyId());
					ps.setLong(3, nps.getAttemptId());
				}

				@Override
				public int getBatchSize() {
					return updatePhoneList.size();
				}
			});
			dsTransactionManager.commit(status);
		} catch (DataAccessException dae) {
			dsTransactionManager.rollback(status);
			throw new TelAppointException(ErrorConstants.ERROR_1014.getErrorCode(), ErrorConstants.ERROR_1014.getUserErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
					dae.toString(), new BaseRequest());
		}
	}

	@Override
	public CampaignMessageSMS getCampaignMessageSMS(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, Long campaignId) throws TelAppointException {
		StringBuilder sql = new StringBuilder();
		sql.append("select * from campaign_message_sms cme Where 1=1");
		sql.append(" and cme.campaign_id=?");
		try {
		return jdbcCustomTemplate.getJdbcTemplate().query(sql.toString(), new Object[] { campaignId }, new ResultSetExtractor<CampaignMessageSMS>() {
			@Override
			public CampaignMessageSMS extractData(ResultSet rs) throws SQLException, DataAccessException {
				CampaignMessageSMS campaignMessageSMS = null;
				while (rs.next()) {
					campaignMessageSMS = new CampaignMessageSMS();
					campaignMessageSMS.setCampaignMessageSMSId(rs.getLong("id"));
					campaignMessageSMS.setCampaignId(rs.getLong("campaign_id"));
					campaignMessageSMS.setLang(rs.getString("lang"));
					campaignMessageSMS.setSubject(rs.getString("subject"));
					campaignMessageSMS.setMessage(rs.getString("message"));
				}
				return campaignMessageSMS;
			}
		});
		} catch (DataAccessException dae) {
			throw new TelAppointException(ErrorConstants.ERROR_1015.getErrorCode(), ErrorConstants.ERROR_1015.getUserErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
					dae.toString(), new BaseRequest());
		}
	}

	public boolean addErrorLog(Logger logger, ErrorLog errorLog) {
		// TODO: save to error_log table.
		return true;
	}
}
