package com.telappoint.notification.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.telappoint.notification.common.model.BaseResponse;

/**
 * 
 * @author Balaji N
 *
 */
@JsonSerialize(include = Inclusion.NON_NULL)
public class FileUploadResponse extends BaseResponse {
	private String totalRecords;
	private String successRecords;
	private String failureRecords;
	private String invalidRecords;
	private String fileUploadResponse;
	private List<String> invalidRecordsList = new ArrayList<String>();
	private String errorResponse;

	public String getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(String totalRecords) {
		this.totalRecords = totalRecords;
	}

	public String getSuccessRecords() {
		return successRecords;
	}

	public void setSuccessRecords(String successRecords) {
		this.successRecords = successRecords;
	}

	public String getFailureRecords() {
		return failureRecords;
	}

	public void setFailureRecords(String failureRecords) {
		this.failureRecords = failureRecords;
	}

	public String getInvalidRecords() {
		return invalidRecords;
	}

	public void setInvalidRecords(String invalidRecords) {
		this.invalidRecords = invalidRecords;
	}

	public String getFileUploadResponse() {
		return fileUploadResponse;
	}

	public void setFileUploadResponse(String fileUploadResponse) {
		this.fileUploadResponse = fileUploadResponse;
	}

	public List<String> getInvalidRecordsList() {
		return invalidRecordsList;
	}

	public void setInvalidRecordsList(List<String> invalidRecordsList) {
		this.invalidRecordsList = invalidRecordsList;
	}

	public String getErrorResponse() {
		return errorResponse;
	}

	public void setErrorResponse(String errorResponse) {
		this.errorResponse = errorResponse;
	}
}
