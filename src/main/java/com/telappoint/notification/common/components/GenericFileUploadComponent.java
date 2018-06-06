package com.telappoint.notification.common.components;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.csvreader.CsvReader;
import com.telappoint.notification.common.clientdb.dao.NotifyDAO;
import com.telappoint.notification.common.constants.PropertiesConstants;
import com.telappoint.notification.common.model.ClientDeploymentConfig;
import com.telappoint.notification.common.model.JdbcCustomTemplate;
import com.telappoint.notification.common.utils.CoreUtils;
import com.telappoint.notification.common.utils.PropertyUtils;
import com.telappoint.notification.constants.FileUploadFieldContants;
import com.telappoint.notification.handlers.exception.TelAppointException;
import com.telappoint.notification.model.FileUploadResponse;
import com.telappoint.notification.model.Notify;
import com.telappoint.notification.model.NotifyPhoneStatus;
import com.telappoint.notification.model.OutboundPhoneLogs;

/**
 * 
 * @author Balaji N
 *
 */
@Component
public class GenericFileUploadComponent {

	@Autowired
	public NotifyDAO notifyResDAO;

	public FileUploadResponse processPhoneLogFile(JdbcCustomTemplate jdbcCustomTemplate, Logger logger, InputStream inputStream, ClientDeploymentConfig cdConfig)
			throws TelAppointException {

		FileUploadResponse fileUploadResponse = new FileUploadResponse();
		BufferedReader br = null;
		CsvReader csvReader = null;
		int successRecords = 0;
		int totalRecords = 0;
		int failureRecords = 0;
		StringBuilder invalidRecords = new StringBuilder();
		int count = 0;
		try {
			br = new BufferedReader(new InputStreamReader(inputStream));
			csvReader = new CsvReader(inputStream, Charset.forName("UTF-8"));
			List<OutboundPhoneLogs> outboundPhoneList = new ArrayList<OutboundPhoneLogs>();
			List<NotifyPhoneStatus> notifyPhoneList = new ArrayList<NotifyPhoneStatus>();
			List<NotifyPhoneStatus> updatePhoneList = new ArrayList<NotifyPhoneStatus>();

			int columns = -1;
			OutboundPhoneLogs outboundLogs = null;
			int batchSize = 500;
			int ignoreHeaders = 1;
			boolean isIgnoredHeader = false;

			Properties props = PropertyUtils.getProperties(PropertiesConstants.PHONE_LOG_FILE_UPLOAD_PROP.getPropertyFileName());
			Map<Integer, String> treePhoneMap = new HashMap<Integer, String>();
			for (final String name : props.stringPropertyNames()) {
				treePhoneMap.put(Integer.valueOf(name), props.getProperty(name));
			}

			while (csvReader.readRecord()) {
				try {
					String line = csvReader.getRawRecord().trim();

					Set<Integer> keys = treePhoneMap.keySet();
					List<Integer> positionList = new ArrayList<Integer>(keys);

					// In fileupload file has row start with QUEUE, then ignore
					// that line.
					if ("".equals(line) || line.startsWith("QUEUE") || line.startsWith("queue")) {
						logger.info("Ignored the line : " + line);
						continue;
					}

					if (isIgnoredHeader == false && ignoreHeaders == 1) {
						isIgnoredHeader = true;
						continue;
					}

					totalRecords++;
					outboundLogs = new OutboundPhoneLogs();
					Timestamp timestamp = new Timestamp(new Date().getTime());
					outboundLogs.setTimeStamp(timestamp.toString());
					outboundLogs.setCallId(1);

					if (columns == -1) {
						columns = csvReader.getColumnCount();
					}

					Integer maxPosition = positionList.get(positionList.size() - 1);

					if (columns < maxPosition) {
						logger.error("Field position is greater than csv file column count.");
						String currentLine = csvReader.getRawRecord();
						currentLine = currentLine.replaceAll(" ", "");
						logger.debug("Invalid Record:" + currentLine);
						logger.debug("Invalid Message: Field position is greater than csv file column count.");
						failureRecords++;
						continue;
					}

					Integer indexOfParameters = CoreUtils.getKeyByValue(treePhoneMap, FileUploadFieldContants.PARAMETERS.getValue());

					String params = csvReader.get(indexOfParameters - 1);
					String param[] = params.split("\\-");

					boolean isNotifyIdValid = false;
					Notify notify = null;
					try {
						notify = notifyResDAO.getNotifyById(jdbcCustomTemplate, Long.parseLong(param[0]));
						outboundLogs.setNotifyId(notify.getNotifyId());
						isNotifyIdValid = true;
					} catch (Exception e) {
						logger.error("Invalid notifyId");
						logger.error("Error:" + e, e);
					}

					NotifyPhoneStatus notifyPhoneStatus = new NotifyPhoneStatus();
					String message = "";

					Integer indexOfPickUps = CoreUtils.getKeyByValue(treePhoneMap, FileUploadFieldContants.PICK_UPS.getValue());
					String pickUps = csvReader.get(indexOfPickUps - 1);

					if (isNotifyIdValid) {
						message = populateOutBoundObject(outboundLogs, logger, notifyPhoneStatus, treePhoneMap, positionList, csvReader, cdConfig, pickUps);
					}

					if ("0".equals(pickUps)) {
						int attemptId = notifyResDAO.getMaxAttemptId(jdbcCustomTemplate, logger, Long.parseLong(param[0]));
						if (attemptId == 0) {
							attemptId = 1;
						} else {
							attemptId = attemptId + 1;
						}
						notifyPhoneStatus.setAttemptId(attemptId);
						notifyPhoneStatus.setNotifyId(notify.getNotifyId());
						notifyPhoneStatus.setCallStatus(1); // No Answer
						notifyPhoneList.add(notifyPhoneStatus);
					} else {
						int attemptId = notifyResDAO.getMaxAttemptId(jdbcCustomTemplate, logger, Long.parseLong(param[0]));
						notifyPhoneStatus.setNotifyId(notify.getNotifyId());
						notifyPhoneStatus.setAttemptId(attemptId);
						updatePhoneList.add(notifyPhoneStatus);
					}

					if ("".equals(message) == false) {
						String currentLine = csvReader.getRawRecord();
						currentLine = currentLine.replaceAll(" ", "");
						logger.debug("Invalid Record:" + currentLine);
						logger.debug("Invalid message: " + message.substring(0, message.length() - 1));
						invalidRecords.append(csvReader.getRawRecord() + "\n");
						fileUploadResponse.getInvalidRecordsList().add(csvReader.getRawRecord());
						failureRecords++;
						continue;
					}

					outboundPhoneList.add(outboundLogs);
					count++;
					if (count >= batchSize) {
						try {
							notifyResDAO.savePhoneRecords(jdbcCustomTemplate, outboundPhoneList);
							notifyResDAO.saveNotifyPhoneStatusRecords(jdbcCustomTemplate, notifyPhoneList);
							notifyResDAO.updateNotifyPhoneStatusSeconds(jdbcCustomTemplate, updatePhoneList);
							successRecords = successRecords + count;
						} catch (Exception e) {
							logger.error("Error:" + e, e);
							failureRecords = failureRecords + count;
						}
						outboundPhoneList.clear();
						notifyPhoneList.clear();
						count = 0;
					}
				} catch (Exception e) {
					failureRecords = failureRecords + 1;
					logger.error("Error:" + e, e);
					continue;
				}
			}
			if (count > 0) {
				try {
					notifyResDAO.savePhoneRecords(jdbcCustomTemplate, outboundPhoneList);
					notifyResDAO.saveNotifyPhoneStatusRecords(jdbcCustomTemplate, notifyPhoneList);
					notifyResDAO.updateNotifyPhoneStatusSeconds(jdbcCustomTemplate, updatePhoneList);
					successRecords = successRecords + count;
				} catch (Exception e) {
					logger.error("Error:" + e, e);
					failureRecords = failureRecords + count;
				}
			}
		} catch (Exception e) {
			logger.error("Error:" + e, e);
		} finally {
			try {
				if(csvReader != null) {
					csvReader.close();
				}
				
				if(br != null) {
					br.close();
				}
			} catch (IOException e) {
				logger.error("Error:" + e, e);
			}
		}
		fileUploadResponse.setTotalRecords(String.valueOf(totalRecords));
		fileUploadResponse.setSuccessRecords(String.valueOf(successRecords));
		fileUploadResponse.setFailureRecords(String.valueOf(failureRecords));
		fileUploadResponse.setInvalidRecords(invalidRecords.toString());
		return fileUploadResponse;
	}

	private String populateOutBoundObject(OutboundPhoneLogs outboundLogs, Logger logger, NotifyPhoneStatus notifyPhoneStatus, Map<Integer, String> treeMap,
			List<Integer> positionList, CsvReader csvReader, ClientDeploymentConfig cdConfig, String pickups) {
		StringBuilder validateMsg = new StringBuilder();
		try {

			for (int pos = 1; pos <= positionList.size(); pos++) {
				String failureMessage = "";
				String fieldName = treeMap.get(pos);
				Object fieldValue = csvReader.get(pos - 1).trim();

				if (FileUploadFieldContants.PHONE_NUMBER.getValue().equals(fieldName)) {
					fieldValue = CoreUtils.removeNonDigits((String) fieldValue);

					if ("".equals(fieldValue) || ((String) fieldValue).length() < 10) {
						failureMessage = "invalid Phone number";
					}
					outboundLogs.setPhone((String) fieldValue);
					notifyPhoneStatus.setPhone((String) fieldValue);
				}

				if (FileUploadFieldContants.DATE_TIME.getValue().equals(fieldName)) {
					try {

						outboundLogs.setCallDate((String) fieldValue);
						notifyPhoneStatus.setCallTimestamp((String) fieldValue);

					} catch (Exception e) {
						logger.error("Error:" + e, e);
						failureMessage = "invalid on call date";
					}
				}

				if (FileUploadFieldContants.ATTEMPTS.getValue().equals(fieldName)) {
					outboundLogs.setAttemptId(Integer.parseInt((String) fieldValue));
				}

				if (FileUploadFieldContants.PICK_UPS.getValue().equals(fieldName)) {
					outboundLogs.setPickups(Integer.parseInt((String) fieldValue));
				}

				if (FileUploadFieldContants.DURATION.getValue().equals(fieldName)) {
					if ("0".equals(pickups) == false) {
						Long calculatedSeconds = (Long.valueOf((String) fieldValue));
						calculatedSeconds = calculatedSeconds + cdConfig.getLagTimeInSeconds() + cdConfig.getLeadTimeInSeconds();
						if (calculatedSeconds < 60) {
							calculatedSeconds = (long) 60;
						} else {
							long quotient = (long) (calculatedSeconds / 60);
							long remainder = (long) (calculatedSeconds % 60);
							calculatedSeconds = ((remainder > 0 ? 1 : 0) + quotient) * 60;
						}
						notifyPhoneStatus.setSeconds(calculatedSeconds.intValue());
					} else {
						notifyPhoneStatus.setSeconds(Integer.valueOf((String) fieldValue));
					}
					outboundLogs.setDuration(Integer.valueOf((String) fieldValue));
				}

				if (FileUploadFieldContants.REASON.getValue().equals(fieldName)) {
					outboundLogs.setReason((String) fieldValue);
				}

				if (FileUploadFieldContants.RESULT.getValue().equals(fieldName)) {
					outboundLogs.setResult((String) fieldValue);
				}

				if (failureMessage.length() > 0) {
					failureMessage = failureMessage + " & ";
					validateMsg.append(failureMessage);
				}
			}

			if (validateMsg.length() == 0) {
				return "";
			}
		} catch (Exception e) {
			logger.error("Error:" + e, e);
			validateMsg.append("Invalid record");
		}
		return validateMsg.toString();
	}
}
