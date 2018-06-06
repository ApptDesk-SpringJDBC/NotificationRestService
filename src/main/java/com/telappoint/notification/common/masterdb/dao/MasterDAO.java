package com.telappoint.notification.common.masterdb.dao;

import java.util.Map;

import org.apache.log4j.Logger;

import com.telappoint.notification.common.model.Client;
import com.telappoint.notification.handlers.exception.TelAppointException;
import com.telappoint.notification.model.SMSConfig;

public interface MasterDAO {
	public  void getClients(final String key, final Map<String, Client> clientCacheMap) throws TelAppointException;
	public void getClientDeploymentConfig(final String key, String clientCode, int clientId,final Map<String, Object> cacheMap) throws TelAppointException;
	public SMSConfig getSMSConfig(Logger logger, Integer clientId) throws TelAppointException;
	public Client getClientByPhone(String smsPhone) throws TelAppointException;
}
