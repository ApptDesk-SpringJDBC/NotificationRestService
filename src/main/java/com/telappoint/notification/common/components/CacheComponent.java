package com.telappoint.notification.common.components;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.telappoint.notification.common.clientdb.dao.NotifyDAO;
import com.telappoint.notification.common.constants.CacheConstants;
import com.telappoint.notification.common.constants.ErrorConstants;
import com.telappoint.notification.common.masterdb.dao.MasterDAO;
import com.telappoint.notification.common.model.BaseRequest;
import com.telappoint.notification.common.model.Client;
import com.telappoint.notification.common.model.ClientDeploymentConfig;
import com.telappoint.notification.handlers.exception.TelAppointException;

/**
 * 
 * @author Balaji Nandarapu
 *
 */

@Component
public class CacheComponent {

	private static Map<String, Client> clientCacheMap = new HashMap<String, Client>();
	private static Map<String, Object> cacheObject = new HashMap<String, Object>();
	private static final Object lock = new Object();

	@Autowired
	private MasterDAO masterDAO;
	
	@Autowired
	private NotifyDAO notifyResvDAO;
	
	public Client getClient(Logger logger, String clientCode, String device, boolean cache) throws TelAppointException {
		StringBuilder key = new StringBuilder();
		key.append(CacheConstants.CLIENT.getValue()).append("|").append(clientCode);
		Client client = clientCacheMap.get(key.toString());
		if (client != null && cache) {
			if (logger != null) {
				logger.debug("Client object returned from cache.");
			}
			return client;
		} else {
			if (logger != null) {
				logger.debug("Client object returned from DB.");
			}
			synchronized (lock) {
				masterDAO.getClients(CacheConstants.CLIENT.getValue(), clientCacheMap);
			}
			client = clientCacheMap.get(key.toString());
			if (client == null) {
				logger.info("Client is not available to process - [clientCode:" + clientCode + ", device:" + device + "]");
				BaseRequest baseRequest = new BaseRequest();
				baseRequest.setClientCode(clientCode);
				baseRequest.setDevice(device);
				throw new TelAppointException(ErrorConstants.ERROR_2998.getErrorCode(), ErrorConstants.ERROR_2998.getUserErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR, "Client not found!", baseRequest.toString());
			}
			return client;
		}
	}

	public ClientDeploymentConfig getClientDeploymentConfig(Logger logger, String clientCode, String device, boolean cache) throws TelAppointException  {
		Client client = getClient(logger, clientCode, device, cache);
		StringBuilder key = new StringBuilder();
		key.append(CacheConstants.CLIENT_DEPLOYMENT_CONFIG.getValue()).append("|").append(clientCode);

		ClientDeploymentConfig clientDeploymentConfig = (ClientDeploymentConfig) cacheObject.get(key.toString());
		if (clientDeploymentConfig != null && cache) {
			logger.debug("ClientDeploymentConfig object returned from cache.");
			return clientDeploymentConfig;
		} else {
			logger.debug("ClientDeploymentConfig object returned from DB.");
			synchronized (lock) {
				masterDAO.getClientDeploymentConfig(CacheConstants.CLIENT_DEPLOYMENT_CONFIG.getValue(), clientCode, client.getClientId(), cacheObject);
			}
			clientDeploymentConfig = (ClientDeploymentConfig) cacheObject.get(key.toString());
			return clientDeploymentConfig;
		}
	}
}
