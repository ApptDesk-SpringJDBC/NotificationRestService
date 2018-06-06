package com.telappoint.notification.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * 
 * @author Balaji N
 *
 */
@JsonSerialize(include = Inclusion.NON_NULL)
public class DynamicTemplatePlaceHolder {
	private String dynamicSelect;
	private String dynamicFrom;
	private String dynamicPlaceHolder;
	private String aliasName;
	private String types;

	public String getDynamicSelect() {
		return dynamicSelect;
	}

	public void setDynamicSelect(String dynamicSelect) {
		this.dynamicSelect = dynamicSelect;
	}

	public String getDynamicFrom() {
		return dynamicFrom;
	}

	public void setDynamicFrom(String dynamicFrom) {
		this.dynamicFrom = dynamicFrom;
	}

	public String getDynamicPlaceHolder() {
		return dynamicPlaceHolder;
	}

	public void setDynamicPlaceHolder(String dynamicPlaceHolder) {
		this.dynamicPlaceHolder = dynamicPlaceHolder;
	}

	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	public String getTypes() {
		return types;
	}

	public void setTypes(String types) {
		this.types = types;
	}

}
