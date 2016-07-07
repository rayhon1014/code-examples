package com.cf.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by ray on 6/17/16.
 */
public class HttpUtilTest {

    @Test
    public void testGetMainDomain(){
        List<String> cases = new ArrayList<String>();
        cases.add("www.google.com");
        cases.add("ww.socialrating.it");
        cases.add("www-01.hopperspot.com");
        cases.add("wwwsupernatural-brasil.blogspot.com");
        cases.add("xtop10.net");
        cases.add("zoyanailpolish.blogspot.com");
        cases.add("zoyanailpolish.blogspot.com.uk");
        int i = 0;
        for(String url: cases)
        {
            cases.set(i, HttpUtil.getMainDomain(url));
            i++;
        }
        assertEquals("wrong domain", "google.com", cases.get(0));
        assertEquals("wrong domain", "socialrating.it", cases.get(1));
        assertEquals("wrong domain", "hopperspot.com", cases.get(2));
        assertEquals("wrong domain", "blogspot.com", cases.get(3));
        assertEquals("wrong domain", "xtop10.net", cases.get(4));
        assertEquals("wrong domain", "blogspot.com", cases.get(5));
        assertEquals("wrong domain", "blogspot.com.uk", cases.get(6));
    }
}
