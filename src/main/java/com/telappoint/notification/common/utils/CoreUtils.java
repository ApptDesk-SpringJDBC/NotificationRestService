package com.telappoint.notification.common.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.telappoint.notification.common.constants.CommonDateContants;

/**
 * 
 * @author Balaji Nandarapu
 *
 */
public class CoreUtils {
	private static DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");

	public static Object getPropertyValue(Object object, String fieldName) throws NoSuchFieldException {
		try {
			BeanInfo info = Introspector.getBeanInfo(object.getClass());
			for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
				if (pd.getName().equals(fieldName)) {
					Method getter = pd.getReadMethod();
					if (getter != null) {
						getter.setAccessible(true);
						return getter.invoke(object, null);
					}

				}
			}
		} catch (Exception e) {
			throw new NoSuchFieldException(object.getClass() + " has no field " + fieldName);
		}
		return "";
	}

	public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
		for (Entry<T, E> entry : map.entrySet()) {
			if (value.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	public static String removeNonDigits(final String str) {
		if (str == null || str.length() == 0) {
			return "";
		}
		return str.replaceAll("\\D+", "");
	}

	public static boolean isStringEqual(String str1, String str2) {
		if (str1 == null || str2 == null)
			return false;
		str1 = str1.trim();
		str2 = str2.trim();
		if (str1.length() == 0 || str2.length() == 0)
			return false;
		if (str1.equals(str2))
			return true;
		return false;
	}

	public static String getNotifyStartDate(int hours, String timeZone) {
		GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone(timeZone));
		cal.add(Calendar.HOUR_OF_DAY, hours);
		ThreadLocal<DateFormat> df = DateUtils.getSimpleDateFormat(CommonDateContants.DATETIME_FORMAT_YYYYMMDDHHMMSS_CAP.getValue());
		df.get().setTimeZone(java.util.TimeZone.getTimeZone(timeZone));
		return df.get().format(cal.getTime());
	}

	public static void setPropertyValue(Object object, String propertyName, Object propertyValue) throws Exception {
		try {
			BeanInfo bi = Introspector.getBeanInfo(object.getClass());
			PropertyDescriptor pds[] = bi.getPropertyDescriptors();
			for (PropertyDescriptor pd : pds) {
				if (pd.getName().equals(propertyName)) {
					Method setter = pd.getWriteMethod();
					if (setter != null) {
						setter.invoke(object, new Object[] { propertyValue });
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public static Object getInitCaseValue(Object value) {
		String name = (String) value;
		StringBuilder nameBuilder = new StringBuilder();
		String[] nameStrs = name.split("\\s+");
		if (nameStrs != null && nameStrs.length > 0) {
			for (String nameStr : nameStrs) {
				if (nameStr != null && !" ".equals(nameStr) && nameStr.length() > 0) {
					nameBuilder.append(nameStr.substring(0, 1) != null ? nameStr.substring(0, 1).toUpperCase() : "");
					nameBuilder.append(nameStr.substring(1));
					nameBuilder.append(" ");
				}
			}
		}
		if (nameBuilder.toString() != null && !"".equals(nameBuilder.toString().trim())) {
			value = nameBuilder.toString().trim();
		}
		return value;
	}

	public static String capitalizeString(String string) {
		char[] chars = string.toLowerCase().toCharArray();
		StringBuilder result = new StringBuilder();
		boolean found = false;
		for (int i = 0; i < chars.length; i++) {
			if (!found && Character.isLetter(chars[i])) {
				result.append(Character.toUpperCase(chars[i]));
				found = true;
			} else if ("'".equals(String.valueOf(chars[i]))) {
				result.append(" ");
				found = false;
			} else if (Character.isWhitespace(chars[i]) || chars[i] == '-') {
				result.append(chars[i]);
				found = false;
			} else if (Character.isLetter(chars[i])) {
				result.append(chars[i]);
			}
		}
		return result.toString();
	}

	public static Date addMinsToCurrentTime(int mins) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, mins);
		return calendar.getTime();
	}

	public static String removeDigitsAndNonAlpha(final String str) {
		if (str == null || str.length() == 0) {
			return "";
		}
		return str.replaceAll("[^A-Za-z]", "");
	}

	private static final String IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	public static boolean validateIP(final String ip) {
		Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
		Matcher matcher = pattern.matcher(ip);
		return matcher.matches();
	}

	static int values[] = { 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 };

	static void printArray(int i) {
		if (i == 0) {
			return;
		} else {
			printArray(i - 1);
		}
		System.out.println("[" + (i - 1) + "] " + values[i - 1]);
		System.out.println(i);
	}

	public static String reverseRecursively(String str) {

		// base case to handle one char string and empty string
		if (str.length() < 2) {
			return str;
		}
		return reverseRecursively(str.substring(1)) + str.charAt(0);
	}

	static String result;

	static String fact(String word, boolean isLastWord) {
		String[] test = word.split(" ");
		if (test.length == 1) {
			return word;
		} else {

			boolean lastWord = (test.length == 1);
			int index = word.indexOf(" ");
			word = word.substring(index + 1);
			result = fact(word, lastWord) + " " + test[0];
			return result;
		}
	}
}
