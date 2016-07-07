package com.cf.util;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by ray on 5/15/16.
 */
public class FileUtilTest {

    @Test
    public void testGetResource() throws Exception{
        String content = FileUtil.readResource("https://raw.githubusercontent.com/rayhon1014/crawlspec/master/index.json");
        assertTrue("Not able to load content", content.length()>-1);

        content = FileUtil.readResource("proxylist.txt");
        assertTrue("Not able to load content", content.indexOf("23.80.164.234:29842")>-1);
    }

}
