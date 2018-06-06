package com.telappoint.notification.common.model;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * @author Balaji N
 *
 */
public class JdbcCustomTemplate {
	private String clientCode;
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate nameParameterJdbcTemplate;
	private DataSourceTransactionManager dataSourceTransactionManager;
	private TransactionTemplate transactionTemplate;
	
	public JdbcCustomTemplate(JdbcTemplate jdbcTemplate,NamedParameterJdbcTemplate nameParameterJdbcTemplate,DataSourceTransactionManager dataSourceTransactionManager,TransactionTemplate transactionTemplate) {
		this.setJdbcTemplate(jdbcTemplate);
		this.setNameParameterJdbcTemplate(nameParameterJdbcTemplate);
		this.setDataSourceTransactionManager(dataSourceTransactionManager);
		this.setTransactionTemplate(transactionTemplate);	
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public NamedParameterJdbcTemplate getNameParameterJdbcTemplate() {
		return nameParameterJdbcTemplate;
	}

	public void setNameParameterJdbcTemplate(NamedParameterJdbcTemplate nameParameterJdbcTemplate) {
		this.nameParameterJdbcTemplate = nameParameterJdbcTemplate;
	}

	
	public String getClientCode() {
		return clientCode;
	}

	public void setClientCode(String clientCode) {
		this.clientCode = clientCode;
	}

	public DataSourceTransactionManager getDataSourceTransactionManager() {
		return dataSourceTransactionManager;
	}

	public void setDataSourceTransactionManager(DataSourceTransactionManager dataSourceTransactionManager) {
		this.dataSourceTransactionManager = dataSourceTransactionManager;
	}

	public TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}

	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

}
