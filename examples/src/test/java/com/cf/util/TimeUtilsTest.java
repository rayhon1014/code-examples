package com.cf.util;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Created by benson on 9/29/14.
 */
public class TimeUtilsTest {
	@Test
	public void testParseDateAndFormatDate() throws ParseException{

		Date date = DateUtils.parseDate("Dec 01 2014", "MMM dd yyyy");
		assertEquals("2014-12-01", TimeUtils.formatDate(date));

		date = DateUtils.parseDate("Dec 01 2014 12:59:58", "MMM dd yyyy HH:mm:ss");
		assertEquals("2014-12-01", TimeUtils.formatDate(date));


		date = DateUtils.parseDate("Expires Dec 1, 2014", "'Expires' MMM d, yyyy");
		assertEquals("2014-12-01", TimeUtils.formatDate(date));

		date = DateUtils.parseDate("Jan 1, 1970", "MMM d, yyyy");
		assertEquals("1970-01-01", TimeUtils.formatDate(date));

		Date endOfDate = TimeUtils.getGMTEndOfDate(date);
		assertEquals(86399000, endOfDate.getTime());

		date = DateUtils.parseDate("Jan 31, 1970", "MMM d, yyyy");
		assertEquals("1970-01-31", TimeUtils.formatDate(date));

		endOfDate = TimeUtils.getGMTEndOfDate(date);
		assertEquals(31l * 24 * 60 * 60 * 1000 - 1000, endOfDate.getTime());    //31 days of seconds minus 1 second
	}
}
