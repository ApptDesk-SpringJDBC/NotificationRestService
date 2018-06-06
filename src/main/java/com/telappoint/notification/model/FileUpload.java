package com.telappoint.notification.model;

/**
 * 
 * @author Balaji N
 *
 */
public class FileUpload {

	private String fileType;
	private long maxFileSizeKB;
	private int ignoreRows;

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public long getMaxFileSizeKB() {
		return maxFileSizeKB;
	}

	public void setMaxFileSizeKB(long maxFileSizeKB) {
		this.maxFileSizeKB = maxFileSizeKB;
	}

	public int getIgnoreRows() {
		return ignoreRows;
	}

	public void setIgnoreRows(int ignoreRows) {
		this.ignoreRows = ignoreRows;
	}
}
