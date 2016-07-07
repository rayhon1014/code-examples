package com.cf.util;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Strings;

/**
 * Created by ray on 8/27/14.
 */
public class HttpUtil {

    public static Map<String, String> getQueryMap(String query)
    {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params)
        {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return map;
    }

    public static String getFullUrl(String url, String siteName)
    {
        if(url!=null && !url.equals("")) {

            if (!url.startsWith("http://") && !url.startsWith("https://")) {

                if (url.startsWith("//")) {
                    return "http:" + url;
                }
                else
                {
                    String tempDetailedUrl = url.replaceAll("^/", "");
                    String domain = siteName.replaceAll("/$", "");

                    if (!domain.startsWith("http")) {
                        domain = "http://" + domain;
                    }

                    return domain + "/" + tempDetailedUrl;
                }
            }
        }
        return url;
    }

    public static String getMainDomain(String url)
    {
        url = url.replaceAll("https://", "").replaceAll("http://","").replaceAll("//","");

        String[] symbols = {"?", "/", "#"};
        for(String symbol : symbols)
        {
            if(url.indexOf(symbol)>-1)
            {
                url = url.substring(0, url.indexOf(symbol));
            }
        }

        String[] parts = url.split("\\.");
        if(parts.length <= 2)
        {
            return url;
        }
        else{
            String mainDomain= "";
            for(int i=1; i< parts.length; i++)
            {
                mainDomain += parts[i];
                if(i< parts.length-1)
                {
                    mainDomain+=".";
                }
            }
            return mainDomain;
        }
    }



}
