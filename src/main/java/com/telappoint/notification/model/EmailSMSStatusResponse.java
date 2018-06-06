package com.telappoint.notification.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.telappoint.notification.common.model.BaseResponse;

/**
 * 
 * @author Balaji
 * 
 */
@JsonSerialize(include = Inclusion.NON_NULL)
public class EmailSMSStatusResponse extends BaseResponse {
	public String pageEmailHeader;
	public String pageContent;

	public String getPageEmailHeader() {
		return pageEmailHeader;
	}

	public void setPageEmailHeader(String pageEmailHeader) {
		this.pageEmailHeader = pageEmailHeader;
	}

	public String getPageContent() {
		return pageContent;
	}

	public void setPageContent(String pageContent) {
		this.pageContent = pageContent;
	}
  
}
