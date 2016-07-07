package com.cf.util;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by ray on 6/14/16.
 */
public class NumberUtilTest {

    @Test
    public void testformatNumber(){
        assertEquals("Wrong number", "1000", NumberUtil.formatNumber("1 k"));
        assertEquals("Wrong number", "1200", NumberUtil.formatNumber("1.2 k"));
        assertEquals("Wrong number", "12300", NumberUtil.formatNumber("12.3k"));
        assertEquals("Wrong number", "200", NumberUtil.formatNumber("0.2 k"));
        assertEquals("Wrong number", "59.57", NumberUtil.formatNumber("59.57"));
    }
}
