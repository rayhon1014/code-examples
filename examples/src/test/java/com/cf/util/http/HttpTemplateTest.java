package com.cf.util.http;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;

/**
 * Created by xiaolongxu on 10/27/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/test-beans.xml"
})
public class HttpTemplateTest {
    @Autowired
    private HttpTemplate httpTemplate;

    @Test
    public void testGetContent() {
        String testUrl = "http://www.amazon.com/s/ref=nb_sb_noss_1?url=search-alias%3Daps&field-keywords=shoes";
        String expectStr = "<title>Amazon.com";

        String content = httpTemplate.getContent(testUrl, false, false).getContent();
        Assert.assertEquals("Result should contain [" + expectStr + "]", true, content.contains(expectStr));

        content = httpTemplate.getContent(testUrl, true, false).getContent();
        Assert.assertEquals("Result should contain [" + expectStr + "]", true, content.contains(expectStr));

        testUrl = "http://www1.macys.com/shop/search?keyword=dress";
        content = httpTemplate.getContent(testUrl, false, false).getContent();
        expectStr = "macys";
        Assert.assertEquals("Result should contain [" + expectStr + "]", true, content.contains(expectStr));

        testUrl = "http://www.tigerdirect.com/applications/category/category_slc.asp?CatId=7060&name=iPad_Accessories&srkey=ipad";
        content = httpTemplate.getContent(testUrl, false, false).getContent();
        expectStr = "tigerdirect";
        Assert.assertEquals("Result should contain [" + expectStr + "]", true, content.contains(expectStr));
    }

    @Test
    public void testDownloadFile() throws Exception {
        String url = "http://code.jquery.com/jquery-1.11.3.min.js";
        String filePath = "/tmp/jquery_downloads/jquery-1.11.3.min.js";
        File file = new File(filePath);

        if (file.exists()) file.delete();
        httpTemplate.downloadFile(url, filePath, false);
        Assert.assertTrue("The file should be downloaded WITHOUT proxy.", file.exists());

        if (file.exists()) file.delete();
        httpTemplate.downloadFile(url, filePath, true);
        Assert.assertTrue("The file should be downloaded WITH proxy.", file.exists());
    }

}
