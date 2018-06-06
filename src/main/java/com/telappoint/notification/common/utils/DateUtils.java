package com.telappoint.notification.common.utils;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.telappoint.notification.common.constants.CommonDateContants;


/**
 * @author Balaji
 * 
 */
public class DateUtils {
	private static final Logger logger = Logger.getLogger(DateUtils.class);

	public static String convert12To24HoursFormat(String twelveHourTime) throws ParseException {
		ThreadLocal<DateFormat> time12Format = getSimpleDateFormat(CommonDateContants.TIME_FORMAT_TWELVE_HRS.getValue());
		ThreadLocal<DateFormat> time24Format = getSimpleDateFormat(CommonDateContants.TIME_FORMAT_TWENTY_FOUR_HRS.getValue());
		return time24Format.get().format(time12Format.get().parse(twelveHourTime));
	}

	public static String convert12To24HoursHHMMSSFormat(String twelveHourTime) throws ParseException {
		ThreadLocal<DateFormat> time12Format = getSimpleDateFormat(CommonDateContants.TIME_FORMAT_TWELVE_HRS.getValue());
		ThreadLocal<DateFormat> time24Format = getSimpleDateFormat(CommonDateContants.TIME_FORMAT_HHMMSS_CAP.getValue());
		return time24Format.get().format(time12Format.get().parse(twelveHourTime));
	}

	public static String convert24To12HoursFormat(String twentyFourHourTime) throws ParseException {
		ThreadLocal<DateFormat> time24Format = getSimpleDateFormat(CommonDateContants.TIME_FORMAT_TWENTY_FOUR_HRS.getValue());
		ThreadLocal<DateFormat> time12Format = getSimpleDateFormat(CommonDateContants.TIME_FORMAT_TWELVE_HRS.getValue());
		return time12Format.get().format(time24Format.get().parse(twentyFourHourTime));
	}
	
	public static Calendar getCurrentCalendarByTimeZone(String timeZone) {
		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone(timeZone));
		return calendar;
	}
	
	public static String convert24To12HMMAFormat(String twentyFourHourTime) throws ParseException {
		ThreadLocal<DateFormat> time24Format = getSimpleDateFormat(CommonDateContants.TIME_FORMAT_TWENTY_FOUR_HRS.getValue());
		ThreadLocal<DateFormat> time12Format = getSimpleDateFormat(CommonDateContants.TIME_FORMAT_TWELVE_HRS.getValue());
		return time12Format.get().format(time24Format.get().parse(twentyFourHourTime));
	}

	
	public static String convert24To12HoursHHMMZZZZ(String twentyFourHourTime) throws ParseException {
		ThreadLocal<DateFormat> time24Format = getSimpleDateFormat(CommonDateContants.TIME_FORMAT_TWENTY_FOUR_HRS.getValue());
		ThreadLocal<DateFormat> time12HHMMZZZZFormat = getSimpleDateFormat(CommonDateContants.TIME_FORMAT_HHMMZZZZ.getValue());
		return time12HHMMZZZZFormat.get().format(time24Format.get().parse(twentyFourHourTime));
	}
	
	public static String convertMMDDYYYY_TO_YYYYMMDDFormat(String mmddyyyDate) throws ParseException {
		ThreadLocal<DateFormat> mmddyyyyFormat = getSimpleDateFormat(CommonDateContants.MM_DD_YYYY_DATE_FORMAT.getValue());
		ThreadLocal<DateFormat> yyyymmddFormat = getSimpleDateFormat(CommonDateContants.DATE_FORMAT_YYYYMMDD.getValue());
		return yyyymmddFormat.get().format(mmddyyyyFormat.get().parse(mmddyyyDate));
	}

	public static String convertDDMMYYYY_TO_YYYYMMDD_Date(String ddmmyyyDate) throws ParseException {
		ThreadLocal<DateFormat> mmddyyyyFormat = getSimpleDateFormat(CommonDateContants.DATE_FORMAT_DDMMYYYY.getValue());
		ThreadLocal<DateFormat> yyyymmddFormat = getSimpleDateFormat(CommonDateContants.DATE_FORMAT_YYYYMMDD.getValue());
		return yyyymmddFormat.get().format(mmddyyyyFormat.get().parse(ddmmyyyDate));
	}

	public static String convertYYYYMMDD_TO_MMDDYYYYFormat(String yyyyddmmDate) throws ParseException {
		ThreadLocal<DateFormat> yyyymmddFormat = getSimpleDateFormat(CommonDateContants.DATE_FORMAT_YYYYMMDD.getValue());
		ThreadLocal<DateFormat> mmddyyyyFormat = getSimpleDateFormat(CommonDateContants.MM_DD_YYYY_DATE_FORMAT.getValue());
		return mmddyyyyFormat.get().format(yyyymmddFormat.get().parse(yyyyddmmDate));
	}
	
	public static String convertYYYYMMDD_TO_FullTextualDayFormat(String yyyyddmmDate) throws ParseException {
		ThreadLocal<DateFormat> yyyymmddFormat = getSimpleDateFormat(CommonDateContants.DATE_FORMAT_YYYYMMDD.getValue());
		ThreadLocal<DateFormat> fullTextualDateFormat = getSimpleDateFormat(CommonDateContants.FULLTEXTUAL_DAY_FORMAT.getValue());
		return fullTextualDateFormat.get().format(yyyymmddFormat.get().parse(yyyyddmmDate));
	}

	public static String convertYYYYMMDD_TO_MMDDYYFormat(String yyyyddmmDate) throws ParseException {
		ThreadLocal<DateFormat> yyyymmddFormat = getSimpleDateFormat(CommonDateContants.DATE_FORMAT_YYYYMMDD.getValue());
		ThreadLocal<DateFormat> mmddyyyyFormat = getSimpleDateFormat(CommonDateContants.MM_DD_YY_DATE_FORMAT.getValue());
		return mmddyyyyFormat.get().format(yyyymmddFormat.get().parse(yyyyddmmDate));
	}

	public static String convertDDMMYYYY_TO_YYYYMMDDFormat(String mmddyyyyDate) throws ParseException {
		ThreadLocal<DateFormat> ddmmyyyyFormat = getSimpleDateFormat(CommonDateContants.DATE_FORMAT_DDMMYYYYHHMMSS_CAP.getValue());
		ThreadLocal<DateFormat> yyyymmddFormat = getSimpleDateFormat(CommonDateContants.DATETIME_FORMAT_YYYYMMDDHHMMSS_CAP.getValue());
		return yyyymmddFormat.get().format(ddmmyyyyFormat.get().parse(mmddyyyyDate));
	}
	
	public static String convertMMDDYYYY_TO_YYYYMMDDHHMMSSFormat(String mmddyyyyDate) throws ParseException {
		ThreadLocal<DateFormat> ddmmyyyyFormat = getSimpleDateFormat(CommonDateContants.DATE_FORMAT_MMDDYYYYHHMMSS_CAP.getValue());
		ThreadLocal<DateFormat> yyyymmddFormat = getSimpleDateFormat(CommonDateContants.DATETIME_FORMAT_YYYYMMDDHHMMSS_CAP.getValue());
		return yyyymmddFormat.get().format(ddmmyyyyFormat.get().parse(mmddyyyyDate));
	}

	public static Timestamp getNextBlockTime(Timestamp original, int blockMinutes) {
		Timestamp later = new Timestamp(original.getTime() + (blockMinutes * 60 * 1000L));
		later.setNanos(original.getNanos());
		return later;
	}

	public static Calendar convertTOCalendar(Timestamp timestamp) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp.getTime());
		return cal;
	}

	public static List<String> convert24To12HoursFormat(List<String> timeList) {
		List<String> time12List = new ArrayList<String>();
		for (String time : timeList) {
			try {
				String convertedTime = DateUtils.convert24To12HoursFormat(time);
				time12List.add(convertedTime);
			} catch (ParseException e) {
				logger.error("Error:" + e, e);
			}
		}
		return time12List;
	}

	public static Calendar dateToCalendar(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

	public static ThreadLocal<DateFormat> getSimpleDateFormat(String dateTimeFormatStr) {
		ThreadLocal<DateFormat> tldf = null;
		try {
			tldf = getThreadLocal(dateTimeFormatStr);
			return tldf;
		} catch (Exception e) {
			logger.error("Error:" + e, e);
		}
		return tldf;
	}

	public static ThreadLocal<DateFormat> getThreadLocal(final String dateTimeForamtStr) {
		final ThreadLocal<DateFormat> tldf_ = new ThreadLocal<DateFormat>() {
			@Override
			protected DateFormat initialValue() {
				return new SimpleDateFormat(dateTimeForamtStr);
			}
		};
		return tldf_;
	}

	public static String getCurrentDate(String format, String timeZone) {
		String dateString = "";
		ThreadLocal<DateFormat> dateFormat = getSimpleDateFormat(format);
		GregorianCalendar gcAppt = new GregorianCalendar(TimeZone.getTimeZone(timeZone));
		dateFormat.get().setCalendar(gcAppt);
		try {
			dateString = dateFormat.get().format(gcAppt.getTime());
		} catch (Exception e) {
			logger.error("Error:" + e, e);
		}
		return dateString;
	}

	public static String getDateStrFrom(String timeZone, int apptDelayTimeDays, int appDelayTimeHours) {
		String dateTimeFormatStr = CommonDateContants.DATETIME_FORMAT_YYYYMMDDHHMMSS_CAP.getValue();
		Calendar calendar = getCalendarFrom(timeZone, apptDelayTimeDays, appDelayTimeHours);
		ThreadLocal<DateFormat> dateFormat = getSimpleDateFormat(dateTimeFormatStr);
		dateFormat.get().setCalendar(calendar);
		String currentDate = dateFormat.get().format(calendar.getTime());
		return currentDate;
	}

	public static Calendar getCalendarFrom(String timeZone, int apptDelayTimeDays, int appDelayTimeHours) {		
		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone(timeZone));
		calendar.add(Calendar.DATE, apptDelayTimeDays);
		calendar.add(Calendar.MINUTE, 0);
		calendar.add(Calendar.HOUR_OF_DAY, appDelayTimeHours);
		calendar.add(Calendar.SECOND, 0);
		calendar.add(Calendar.MILLISECOND, 0);
		return calendar;
	}
	
	public static Calendar getCalendarFromForDateFetch(String timeZone, int apptDelayTimeDays, int appDelayTimeHours) {	
		
		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone(timeZone));
		System.out.println("from ==> before:"+calendar.getTime());
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		System.out.println("from =set 0=> before:"+calendar.getTime());
		int hour = 0;
		int days = 0;
		if(apptDelayTimeDays > 0) {
			hour = 0;
			days = apptDelayTimeDays;
		} else {
			hour = appDelayTimeHours;
			days = 0;
		}
		calendar.add(Calendar.DATE, days);
		calendar.add(Calendar.HOUR, hour);
		System.out.println("from ==> after:"+calendar.getTime());
		return calendar;
	}

	public static String getDateStrTO(String timeZone,int apptDelayTimeDays, int appDelayTimeHours, int maxApptDurationDays) {
		String dateTimeFormatStr = CommonDateContants.DATETIME_FORMAT_YYYYMMDDHHMMSS_CAP.getValue();
		Calendar calendar = getCalendarTO(timeZone,apptDelayTimeDays,appDelayTimeHours, maxApptDurationDays);
		ThreadLocal<DateFormat> dateFormat = getSimpleDateFormat(dateTimeFormatStr);
		dateFormat.get().setCalendar(calendar);
		String date = dateFormat.get().format(calendar.getTime());
		return date;
	}

	public static Calendar getCalendarTO(String timeZone,int apptDelayTimeDays, int appDelayTimeHours, int maxApptDurationDays) {
		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone(timeZone));
		System.out.println("to ==> before:"+calendar.getTime());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND,0);
		int maxDays = maxApptDurationDays;
		calendar.add(Calendar.DATE, maxDays);
		System.out.println("to ==> after:"+calendar.getTime());
		return calendar;
	}
	/**
	 * @param dateCalendar
	 * @param formatDateOrTimeStr
	 *            - date format might be any thing like eg: "yyyy-MM-dd" or
	 *            HH:mm:ss or HH:mm(twenty four time format) or hh:mma (twelve
	 *            time format)
	 * @return returns date string by formatting based on formatDateOrTimeStr
	 *         format.
	 */
	public static String formatGCDateToDateString(Calendar dateCalendar, String formatDateOrTimeStr) {
		String dateString = "";
		ThreadLocal<DateFormat> dateFormat = getSimpleDateFormat(formatDateOrTimeStr);
		dateFormat.get().setCalendar(dateCalendar);
		try {
			dateString = dateFormat.get().format(dateCalendar.getTime());
		} catch (Exception e) {
		}
		return dateString;
	}

	public static Set<String> getDateTimesSet(String dateTime, int blockInMins, int blocks) {
		Set<String> dateTimes = new HashSet<String>();
		dateTimes.add(dateTime);
		GregorianCalendar gcal = formatSqlStringToGC(dateTime);
		for (int i = 1; i < blocks; i++) {
			addMinutesAndGetCalendar(gcal, blockInMins);
			dateTimes.add(formatGCDateToYYYYMMDD(gcal) + " " + formatGCDateToHH_MM_SS(gcal));
		}
		return dateTimes;
	}

	/** dateStr = eg: 2013-03-21 00:00:00 */
	public static GregorianCalendar formatSqlStringToGC(String datestr) {
		int year = Integer.parseInt(datestr.substring(0, 4));
		int month = Integer.parseInt(datestr.substring(5, 7));
		int date = Integer.parseInt(datestr.substring(8, 10));
		int hour = Integer.parseInt(datestr.substring(11, 13));
		int min = Integer.parseInt(datestr.substring(14, 16));
		int sec = Integer.parseInt(datestr.substring(17, 19));

		GregorianCalendar cal = new GregorianCalendar();

		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DATE, date);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, min);
		cal.set(Calendar.SECOND, sec);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}
	
	public static String getCurrentDateYYYYMMDD(String timeZone) {
		ThreadLocal<DateFormat> df = getSimpleDateFormat("yyyy-MM-dd");
		df.get().setTimeZone(TimeZone.getTimeZone(timeZone));
		String todayDate = df.get().format(new Date());
		return todayDate;
	}
	
	public static String getDateYYYYMMDD(String timeZone,Date date) {
		ThreadLocal<DateFormat> df = getSimpleDateFormat("yyyy-MM-dd");
		df.get().setTimeZone(TimeZone.getTimeZone(timeZone));
		String todayDate = df.get().format(date);
		return todayDate;
	}
	
	public static String getDate(String timeZone,Date date,String format) {
		ThreadLocal<DateFormat> df = getSimpleDateFormat(format);
		df.get().setTimeZone(TimeZone.getTimeZone(timeZone));
		String todayDate = df.get().format(date);
		return todayDate;
	}
	
	public static String getCurrentDateByFormat(String timeZone,String format) {
		ThreadLocal<DateFormat> df = getSimpleDateFormat(format);
		df.get().setTimeZone(TimeZone.getTimeZone(timeZone));
		String todayDate = df.get().format(new Date());
		return todayDate;
	}
	
	public static boolean isTodayDate(String timeZone,String passedDate) {
		ThreadLocal<DateFormat> df = getSimpleDateFormat("yyyy-MM-dd");
		df.get().setTimeZone(TimeZone.getTimeZone(timeZone));
		String todayDate = df.get().format(new Date());
		if(passedDate.equals(todayDate)) {
			return true;
		} else {
			return false;
		}
	}

	/** dateStr = eg: 00:00:00, HH:mm:ss */
	public static GregorianCalendar formatHHMMSSStringToGC(String timestr) {
		int hour = Integer.parseInt(timestr.substring(0, 2));
		int min = Integer.parseInt(timestr.substring(3, 5));
		int sec = Integer.parseInt(timestr.substring(6, 8));
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, min);
		cal.set(Calendar.SECOND, sec);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}
	
	/** dateStr : yyyy:mm:dd */
	public static GregorianCalendar formatYYYYMMDDToGC(String datestr) {
		int year = Integer.parseInt(datestr.substring(0, 4));
		int month = Integer.parseInt(datestr.substring(5, 7));
		int date = Integer.parseInt(datestr.substring(8, 10));
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DATE, date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	/** dateStr : yyyy:mm:dd */
	public static GregorianCalendar formatYYYYMMDDToGC(String datestr, String timeZone) {
		int year = Integer.parseInt(datestr.substring(0, 4));
		int month = Integer.parseInt(datestr.substring(5, 7));
		int date = Integer.parseInt(datestr.substring(8, 10));
		GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone(timeZone));
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DATE, date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	public static int compareTime(String time1, String time2) {
		int retval = -2;
		if (time1 == null && time2 == null) {
			retval = 0;
		} else if (time1 != null && time2 != null) {
			// hh:mm:ss
			int hour1 = Integer.valueOf(time1.substring(0, 2)).intValue();
			int min1 = Integer.valueOf(time1.substring(3, 5)).intValue();
			int sec1 = Integer.valueOf(time1.substring(6, 8)).intValue();

			// hh:mm:ss
			int hour2 = Integer.valueOf(time2.substring(0, 2)).intValue();
			int min2 = Integer.valueOf(time2.substring(3, 5)).intValue();
			int sec2 = Integer.valueOf(time2.substring(6, 8)).intValue();

			int timeInSeconds1 = hour1 * 60 * 60 + min1 * 60 + sec1;
			int timeInSeconds2 = hour2 * 60 * 60 + min2 * 60 + sec2;

			if (timeInSeconds1 == timeInSeconds2)
				retval = 0;
			else if (timeInSeconds1 < timeInSeconds2)
				retval = -1;
			else
				retval = 1;
		}
		return retval;
	}

	public static boolean isTimeWithinBoundary(String apptTime, String st1, String et1) {
		boolean retval = true;
		if (apptTime != null && st1 != null && et1 != null) {
			if (compareTime(apptTime, st1) == -1)
				retval = false; // apptTime < st1
			else if (compareTime(apptTime, et1) != -1)
				retval = false; // apptTime >= et1
		}
		return retval;
	}

	public static String addTimeSlot(String dateTime, int minutes) {
		GregorianCalendar slot = DateUtils.formatSqlStringToGC(dateTime);
		slot.add(Calendar.MINUTE, minutes);
		return new String(formatGCDateToYYYYMMDD(slot) + " " + formatGCDateToHH_MM_SS(slot));
	}

	public static String formatGCDateToHH_MM_SS(Calendar dateGC) {
		String dateString = "";
		SimpleDateFormat formatter = new SimpleDateFormat(CommonDateContants.TIME_FORMAT_HHMMSS_CAP.getValue());
		formatter.setCalendar(dateGC);
		try {
			dateString = formatter.format(dateGC.getTime());
		} catch (Exception e) {
			logger.error("Exception :" + e.toString());
		}
		return dateString;
	}

	public static String formatGCDateToHH_MM(Calendar dateGC) {
		String dateString = "";
		SimpleDateFormat formatter = new SimpleDateFormat(CommonDateContants.TIME_FORMAT_TWENTY_FOUR_HRS.getValue());
		formatter.setCalendar(dateGC);
		try {
			dateString = formatter.format(dateGC.getTime());
		} catch (Exception e) {
			logger.error("Exception :" + e.toString());
		}
		return dateString;
	}

	public static String formatGCDateToAM_PM(Calendar dateGC) {
		String dateString = "";
		ThreadLocal<DateFormat> formatter = getSimpleDateFormat(CommonDateContants.DATE_FORMAT_YYYYMMDD.getValue());
		formatter.get().setCalendar(dateGC);
		try {
			dateString = formatter.get().format(dateGC.getTime());
		} catch (Exception e) {
			logger.error("Exception :" + e.toString());
		}
		return dateString;
	}

	public static String formatGCDateToYYYYMMDD(Calendar dateCal) {
		String dateString = "";
		ThreadLocal<DateFormat> formatter = getSimpleDateFormat(CommonDateContants.DATE_FORMAT_YYYYMMDD.getValue());
		formatter.get().setCalendar(dateCal);
		try {
			dateString = formatter.get().format(dateCal.getTime());
		} catch (Exception e) {
			logger.error("Exception :" + e.toString());
		}
		return dateString;
	}

	/**
	 * This method checks to see if the supplied dateValue format matches the
	 * supplied dateFormat.
	 */
	public static boolean isValidDate(String dateValue, String dateFormatStr, String timeZone) {
		GregorianCalendar gcDate = new GregorianCalendar(TimeZone.getTimeZone(timeZone));
		ThreadLocal<DateFormat> dateFormat = getSimpleDateFormat(dateFormatStr);
		dateFormat.get().setCalendar(gcDate);
		try {
			dateFormat.get().setLenient(false);
			dateFormat.get().parse(dateValue);
		} catch (Exception e) {
			logger.error("Error :" + e, e);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// not using yet
	public static Calendar addMinutesAndGetCalendar(int minutes, String dataFormatStr) {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.MINUTE, minutes);
		return cal;
	}

	public static Calendar addDaysAndGetCalendar(GregorianCalendar cal, int days, String timeZone) {
		cal.setTimeZone(TimeZone.getTimeZone(timeZone));
		cal.add(Calendar.DATE, days);
		return cal;
	}

	public static Calendar addDaysAndGetCalendar(int days) {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DATE, days);
		return cal;
	}

	public static GregorianCalendar addMonthsAndGetCalendar(int months) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.add(Calendar.MONTH, months);
		return cal;
	}

	public static Calendar addHoursAndGetCalendar(Calendar calendar, int hours) {
		calendar.add(Calendar.HOUR, hours);
		return calendar;
	}

	public static Calendar addDaysAndGetCalendar(Calendar calendar, int days) {
		calendar.add(Calendar.DATE, days);
		return calendar;
	}

	public static Calendar addMonthsAndGetCalendar(Calendar calendar, int months) {
		calendar.add(Calendar.MONTH, months);
		return calendar;
	}

	public static GregorianCalendar addHoursAndGetCalendar(GregorianCalendar gcalendar, int hours, String timeZone) {
		gcalendar.setTimeZone(TimeZone.getTimeZone(timeZone));
		gcalendar.add(Calendar.HOUR, hours);
		return gcalendar;
	}

	public static String getTimeStr(Time time, String timeFormat) {
		ThreadLocal<DateFormat> dateFormat = getSimpleDateFormat(timeFormat);
		return dateFormat.get().format(time);
	}

	public static boolean isSameDay(Calendar cal1, Calendar cal2) {
		if (cal1 == null || cal2 == null) {
			throw new IllegalArgumentException("The dates must not be null");
		}
		return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2
				.get(Calendar.DAY_OF_YEAR));
	}

	// not using yet
	public static boolean isToday(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return isSameDay(calendar, Calendar.getInstance());
	}

	public static boolean isToday(Calendar cal) {
		return isSameDay(cal, Calendar.getInstance());
	}

	public static Calendar addMinutesAndGetCalendar(Calendar calendar, int minutes) {
		calendar.add(Calendar.MINUTE, minutes);
		return calendar;
	}

	public static Calendar addMinutesAndGetCalendar(Calendar calendar, int minutes, String timeZone) {
		calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
		calendar.add(Calendar.MINUTE, minutes);
		return calendar;
	}

	// not using yet
	public static Calendar addMinutesAndGetCalendar(int minutes, String dateFormatStr, String timeZone) {
		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone(timeZone));
		cal.add(Calendar.MINUTE, minutes);
		return cal;
	}

	// not using yet
	public static Date addDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);
		return cal.getTime();
	}

	public static Timestamp getTimestampFromString(String dateTimeString) {
		Date date = null;
		java.sql.Timestamp timeStampDate = null;
		if (dateTimeString != null) {
			try {
				ThreadLocal<DateFormat> dateFormat = getSimpleDateFormat(CommonDateContants.DATETIME_FORMAT_YYYYMMDDHHMMSS_CAP.getValue());
				date = dateFormat.get().parse(dateTimeString.trim());
				timeStampDate = new Timestamp(date.getTime());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return timeStampDate;
	}

	public static Timestamp getTimestampFromString(String dateTimeString, String format) {
		Date date = null;
		java.sql.Timestamp timeStampDate = null;
		if (dateTimeString != null) {
			try {
				ThreadLocal<DateFormat> dateFormat = getSimpleDateFormat(format);
				date = dateFormat.get().parse(dateTimeString.trim());
				timeStampDate = new Timestamp(date.getTime());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return timeStampDate;
	}
	
	
	public static Date getDateFromString(String dateTimeString) {
		Date date = null;
		if (dateTimeString != null) {
			try {
				ThreadLocal<DateFormat> dateFormat = getSimpleDateFormat(CommonDateContants.MM_DD_YYYY_DATE_FORMAT.getValue());
				date = dateFormat.get().parse(dateTimeString.trim());
				return date;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return date;
	}

	public static Timestamp getCurrentTimeStampYYYYMMDDHHMMSS(String timeZone) {
		String currentDate = DateUtils.getCurrentDate(CommonDateContants.DATETIME_FORMAT_YYYYMMDDHHMMSS.getValue(), timeZone);
		Calendar cal = DateUtils.formatSqlStringToGC(currentDate);
		Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
		return timestamp;
	}
	
	public static Timestamp getCurrentTimeStampYYYYMMDDHHMMSS(String timeZone,int days) {
		String currentDate = DateUtils.getCurrentDate(CommonDateContants.DATETIME_FORMAT_YYYYMMDDHHMMSS.getValue(), timeZone);
		Calendar cal = DateUtils.formatSqlStringToGC(currentDate);
		cal.add(Calendar.DATE, days);
		Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
		return timestamp;
	}

	public static Timestamp getCurrentTimeStampYYYYMMDDHHMMSSCAPS(String timeZone) {
		String currentDate = DateUtils.getCurrentDate(CommonDateContants.DATETIME_FORMAT_YYYYMMDDHHMMSS_CAP.getValue(), timeZone);
		Calendar cal = DateUtils.formatSqlStringToGC(currentDate);
		Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
		return timestamp;
	}

	public static Timestamp getCurrentTimeStampYYYYMM00_00_00(String timeZone) {
		String currentDate = DateUtils.getCurrentDate(CommonDateContants.DATETIME_FORMAT_YYYYMMDDHHMMSS.getValue(), timeZone);
		Calendar cal = DateUtils.formatSqlStringToGC(currentDate);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
		return timestamp;
	}

	public static Timestamp getTimeStamp_23_59_59(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 0);
		Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
		return timestamp;
	}

	public static Timestamp getCurrentTimeStampYYYYMM23_59_59(String timeZone) {
		String currentDate = DateUtils.getCurrentDate(CommonDateContants.DATETIME_FORMAT_YYYYMMDDHHMMSS.getValue(), timeZone);
		Calendar cal = DateUtils.formatSqlStringToGC(currentDate);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 0);
		Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
		return timestamp;
	}

	/**
	 * @param dateTimeString
	 * @return
	 */
	public static Date getDateObject(String dateTimeString) {
		Date date = null;
		if (dateTimeString != null) {
			try {
				ThreadLocal<DateFormat> dateFormat = getSimpleDateFormat(CommonDateContants.DATE_FORMAT_YYYYMMDD.getValue());
				date = dateFormat.get().parse(dateTimeString.trim());
				return date;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return date;
	}

	/**
	 * @param dateTimeString
	 * @param dateFormat
	 * @return
	 */
	public static Date getDateObject(String dateTimeString, String dateFormat) {
		Date date = null;
		if (dateTimeString != null) {
			try {
				ThreadLocal<DateFormat> format = getSimpleDateFormat(dateFormat);
				date = format.get().parse(dateTimeString.trim());
				return date;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return date;
	}

	public static List<String> getCurrentWeekDateList(Calendar calendar, String timeZone) {
		List<String> weeklyKeys = new ArrayList<String>();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		Calendar currentDate = calendar;
		Calendar startDate = calendar;

		int firstDayOfWeek = currentDate.getFirstDayOfWeek();
		int days = (startDate.get(Calendar.DAY_OF_WEEK) + 7 - firstDayOfWeek) % 7;
		startDate.add(Calendar.DATE, -days);

		Calendar endDate = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
		endDate.setTime(startDate.getTime());
		endDate.add(Calendar.DATE, 6);

		ThreadLocal<DateFormat> dtf = DateUtils.getSimpleDateFormat(CommonDateContants.DATE_FORMAT_YYYYMMDD.getValue());
		ThreadLocal<DateFormat> dayOfTheWeek = DateUtils.getSimpleDateFormat(CommonDateContants.DAY_OF_THE_WEEK_FORMAT.getValue());
		weeklyKeys.add(dtf.get().format(startDate.getTime()) + "|" + dayOfTheWeek.get().format(startDate.getTime()));
		while (startDate.before(endDate)) {
			startDate.add(Calendar.DATE, 1);
			String key = dtf.get().format(startDate.getTime()) + "|" + dayOfTheWeek.get().format(startDate.getTime());
			weeklyKeys.add(key);
		}
		return weeklyKeys;
	}

	public static String getDateString(String dateFormat, int days, String timeZone) {
		Calendar cal = null;
		if (timeZone != "" && timeZone != null) {
			cal = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
		} else {
			cal = Calendar.getInstance();
		}
		cal.add(Calendar.DATE, days);
		ThreadLocal<DateFormat> threadLocal = getSimpleDateFormat(dateFormat);
		return threadLocal.get().format(cal.getTime());
	}

	public static String getDateString(Date date, String dateFormat, int days, String timeZone) {
		Calendar cal = null;
		if (timeZone != "" && timeZone != null) {
			cal = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
		} else {
			cal = Calendar.getInstance();
		}
		cal.setTime(date);
		cal.add(Calendar.DATE, days);
		ThreadLocal<DateFormat> threadLocal = getSimpleDateFormat(dateFormat);
		return threadLocal.get().format(cal.getTime());
	}
	
	public static String getDateStringFromDate(Date date,String format) {
		String dateTimeString = "";
		if (date != null) {
			try {
				ThreadLocal<DateFormat> dateFormat = getSimpleDateFormat(format);
				dateTimeString = dateFormat.get().format(date);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return dateTimeString;
	}

	public static String getDateString_YYYYMMDD_FromDate(Date date) {
		String dateTimeString = "";
		if (date != null) {
			try {
				ThreadLocal<DateFormat> dateFormat = getSimpleDateFormat(CommonDateContants.DATE_FORMAT_YYYYMMDD.getValue());
				dateTimeString = dateFormat.get().format(date);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return dateTimeString;
	}

	public static String getStringFromDate(Date date, String formate) {
		String dateTimeString = "";
		if (date != null) {
			try {
				ThreadLocal<DateFormat> dateFormat = getSimpleDateFormat(formate);
				dateTimeString = dateFormat.get().format(date);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return dateTimeString;
	}

	public static Date getDateFromString(String dateTimeString, String formate) {
		Date date = null;
		if (dateTimeString != null) {
			try {
				ThreadLocal<DateFormat> dateFormat = getSimpleDateFormat(formate);
				date = dateFormat.get().parse(dateTimeString);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return date;
	}

	public static Calendar setDateAndGetCalendar(String dateStr, String dateFormatStr, String timeZone) {
		Calendar cal = null;
		if (timeZone != "" && timeZone != null) {
			cal = new GregorianCalendar(TimeZone.getTimeZone(timeZone));
		} else {
			cal = new GregorianCalendar();
		}
		Date date = getDateFromString(dateStr, dateFormatStr);
		if (date != null) {
			cal.setTime(date);
		}
		return cal;
	}

	public static Calendar setDateAndGetCalendar(Timestamp timestamp, String dateFormatStr) {
		Calendar cal = Calendar.getInstance();
		Date date = new Date(timestamp.getTime());
		if (date != null) {
			cal.setTime(date);
		}
		return cal;
	}

	public static String getWeekDay(Date date) {
		String dayOfWeek = "";
		try {
			// Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
			// Then get the day of week from the Date based on specific locale.
			dayOfWeek = new SimpleDateFormat("EEE").format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dayOfWeek;
	}

	
	enum MonthDay {
		JANUARY(1, 31), FEBRUARY(2, 28), MARCH(3, 31), APRIL(4, 30), MAY(5, 31), JUNE(6, 30), JULY(7, 31), AUGUST(8, 31), SEPTEMBER(9, 30), OCTOBER(10, 31), NOVEMBER(11, 30), DECEMBER(
				12, 31);

		int month;
		int days;

		MonthDay(int month, int days) {
			this.month = month;
			this.days = days;
		}

		int getMonth() {
			return month;
		}

		int getDays() {
			return days;
		}

		public static MonthDay from(int month) {
			for (MonthDay monthDay : values()) {
				if (monthDay.month == month)
					return monthDay;
			}
			return null;
		}
	}

	public static Time prepareTimeFormString(String time) {
		Time sqlTime = null;
		if (time != null && !"".equals(time)) {
			if (time.endsWith("AM") || time.endsWith("PM")) {
				String AM_PM = time.substring(time.length() - 2).trim();

				time = time.substring(0, time.length() - 2);

				if (time != null && !"".equals(time)) {
					time = time.trim().replace(":", ".");
					Double timeInDouble = Double.parseDouble(time);
					if ("AM".equals(AM_PM)) {
						if (timeInDouble >= 12.00) {
							timeInDouble = timeInDouble + 12;
						}
					} else if ("PM".equals(AM_PM)) {
						if (timeInDouble < 12.00) {
							timeInDouble = timeInDouble + 12;
						}
					}
					time = Double.toString(timeInDouble);
					time = time.replace(".", ":");
					StringBuilder timeBuilder = new StringBuilder();
					String[] timeStrArray = time.split(":");
					if (timeStrArray[0].length() == 1) {
						timeBuilder.append("0");
						timeBuilder.append(timeStrArray[0]);
					} else {
						timeBuilder.append(timeStrArray[0]);
					}
					timeBuilder.append(":");
					if (timeStrArray[1].length() == 1) {
						timeBuilder.append(timeStrArray[1]);
						timeBuilder.append("0");
					} else {
						timeBuilder.append(timeStrArray[1]);
					}
					timeBuilder.append(":00");
					sqlTime = Time.valueOf(timeBuilder.toString());
				}
			} else if (time.length() == 8) {
				time = time.substring(0, 8);
				sqlTime = Time.valueOf(time);
			}
		}
		return sqlTime;
	}

	public static void main(String... args) throws Exception {
		System.out.println("::"+getCurrentDate("yyyy-MM-dd HH:mm:ss","US/Central"));
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = df.parse("2014-12-09");
		
		System.out.println(DateUtils.getSimpleDateFormat("EEEEE, MMM d, yyyy").get().format(startDate));
	}
}
