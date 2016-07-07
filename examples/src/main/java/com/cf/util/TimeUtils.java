package com.cf.util;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by xiaolongxu on 9/18/14.
 */
public class TimeUtils {

	public static List<String> getDateList(String startTimeStr, String endTimeStr, DateTimeFormatter dateTimeFormatter) {
        List<String> dateList = new ArrayList<String>();

        DateTime startDateTime = dateTimeFormatter.parseDateTime(startTimeStr);
        // Use startTime as default for endTime
        DateTime endDateTime = StringUtils.isNotBlank(endTimeStr) ? dateTimeFormatter.parseDateTime(endTimeStr) : startDateTime;

        DateTime dateTime = new DateTime(startDateTime);
        while (dateTime.isBefore(endDateTime) || dateTime.isEqual(endDateTime)) {
            dateList.add(dateTime.toString(dateTimeFormatter));
            dateTime = dateTime.plusDays(1);
        }

        return dateList;
    }

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public static String formatDate(Date date){
		return date == null? null : dateFormat.format(date);
	}

	public static Date parseDate(String dateString, String pattern) throws ParseException {
		return StringUtils.isBlank(dateString) ? null : DateUtils.parseDate(dateString, new String[]{pattern});
	}

	public static Date getGMTEndOfDate(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("GMT"));
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		cal.set(year, month, day, 23, 59, 59);
		return cal.getTime();
	}
}
