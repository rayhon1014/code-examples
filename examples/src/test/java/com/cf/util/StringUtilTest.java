package com.cf.util;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by benson on 9/29/14.
 */
public class StringUtilTest {
	@Test
	public void testFormatCurrency(){
		assertEquals("1009.12", StringUtil.formatCurrency(1009.12f));
		assertEquals("10001", StringUtil.formatCurrency(10001f));
		assertEquals("1000.1", StringUtil.formatCurrency(1000.1f));
		assertNull(StringUtil.formatCurrency(null));

		assertEquals("1009.13", StringUtil.formatCurrency(1009.129f));

		assertEquals("1009.12", StringUtil.formatCurrency(1009.125f));
		assertEquals("1009.13", StringUtil.formatCurrency(1009.126f));
	}

	@Test
	public void testParseCurrency(){
		Float num = StringUtil.parseCurrency("1009.12");
		assertNotNull(num);
		assertEquals("1009.12", num.toString());

		num = StringUtil.parseCurrency("1009.126");
		assertNotNull(num);
		assertEquals("1009.126", num.toString());
	}

    @Test
	public void testSplitToMap(){
        String nameValuePairStr = "category=laptop |brand=brand A |  age=5 ";
        Map<String, String> nameValuePairs = StringUtil.splitToMap(nameValuePairStr, "|");
        assertEquals("Category is not match", nameValuePairs.get("category"), "laptop");
        assertEquals("Brand is not match", nameValuePairs.get("brand"), "brand A");
        assertEquals("Age is not match", nameValuePairs.get("age"), "5");
    }
}
