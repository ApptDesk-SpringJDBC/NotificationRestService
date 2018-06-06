package com.telappoint.notification.common.utils;

import java.util.Set;

public class StringUtils {
	
	private StringUtils() {	
	}
	
	public static boolean isEmpty(String input) {
		return input == null || input.trim().length() == 0;
	}

	public static <T> String concatWithSeparator(Set<T> set, String separator) {
		StringBuilder idStr = new StringBuilder();
		for (T t : set) {
			if (idStr.length() > 0) {
				idStr.append(separator);
				idStr.append(t);
			} else {
				idStr.append(t);
			}
		}
		return idStr.toString();
	}

	public static String singleQuoteString(String input, String delimitor) {
		String loginSplit[] = input.split("\\" + delimitor);
		StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < loginSplit.length; i++) {
			sb.append("'" + loginSplit[i] + "'");
			if (i + 1 < loginSplit.length == true) {
				sb.append(",");
			}
		}
		return sb.toString();
	}

	/**
	 * Returns true if string is not empty and not null
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isNotEmpty(String input) {
		return !isEmpty(input);
	}

	public static String formatPhoneNumber(String phone) throws Exception {
		StringBuilder buf = new StringBuilder("");
		if (isNotEmpty(phone)) {
			try {
				buf.append(phone.substring(0, 3));
				buf.append("-");
				buf.append(phone.substring(3, 6));
				buf.append("-");
				buf.append(phone.substring(6, 10));
			} catch (Exception e) {
				
			}
		}

		return buf.toString();
	}
}