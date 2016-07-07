package com.cf.util;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: rayhon
 * Date: 9/14/12
 * Time: 9:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class HttpRequestSimulator {
    public static String[] userAgents = new String[]{
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:33.0) Gecko/20100101 Firefox/33.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_4) AppleWebKit/537.78.2 (KHTML, like Gecko) Version/7.0.6 Safari/537.78.2",
            "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)"

//            "Mozilla/5.0 (Windows; U; Windows NT 5.0; en-US; rv:1.9.0.10) Gecko/2009042316 Firefox/3.0.10",
//            "Opera/9.52 (Windows NT 5.0; U; en)",
//            "Mozilla/5.0 (Windows; U; Windows NT 5.0; en-US; rv:1.7.5) Gecko/20060912 Netscape/8.1.2",
//            "Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9.0.1) Gecko/2008070208 Firefox/3.0.1",
//            "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727; .NET CLR 3.0.04506.648; .NET CLR 3.5",
//            "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727; .NET CLR 3.0.04506.648; .NET CLR 3.5",
//            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)",
//            "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.10) Gecko/2009042523 Ubuntu/9.04 (jaunty) Firefox/3.0.10",
//            "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; .NET CLR 2.0.50727; .NET CLR 3.0.04506.648; .NET CLR 3.5.21022; MS-RT",
//            "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727; .NET CLR 3.0.04506.648; .NET CLR 3.5",
//            "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-TW; rv:1.9.0.10) Gecko/2009042316 Firefox/3.0.10 (.NET CLR 3.5.30729)",
//            "Opera/9.60 (J2ME/MIDP; Opera Mini/4.2.13057/724; U; en) Presto/2.2.0",
//            "Mozilla/5.0 (YahooYSMcm/3.0.0; http://help.yahoo.com)",
//            "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US) AppleWebKit/525.19 (KHTML, like Gecko) Chrome/1.0.154.64 Safari/525.19",
//            "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US) AppleWebKit/525.19 (KHTML, like Gecko) Chrome/1.0.154.59 Safari/525.19",
//            "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.0.10) Gecko/2009042316 Firefox/3.0.10 (.NET CLR 3.5.30729)",
//            "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0; SLCC1; .NET CLR 2.0.50727; Media Center PC 5.0; .NET CLR",
//            "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.4506",
//            "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; GTB6; SLCC1; .NET CLR 2.0.50727; Media Center PC 5.0; .NET CLR 3.5.21",
//            "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; GTB6; Embedded Web Browser from: http://bsalsa.com/; .N",
//            "Mozilla/5.0 (Windows; U; Windows NT 5.1; ru; rv:1.9.0.10) Gecko/2009042316 Firefox/3.0.10 (.NET CLR 3.5.30729)"
    };

    public static String getUserAgent()
    {
        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(userAgents.length);
        return userAgents[randomInt];
    }

}
