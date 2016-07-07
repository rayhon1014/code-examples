package com.cf.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ray on 4/15/14.
 */
public class CookieUtils {
    private static final Logger LOGGER = Logger.getLogger(CookieUtils.class);

    final static class CookieDateFormat extends SimpleDateFormat {

        CookieDateFormat() {
            super("E, dd-MMM-yyyy HH:mm:ss z", Locale.ENGLISH);
            setTimeZone(TimeZone.getTimeZone("GMT"));
        }
    }

    public static String getExpiredTime(int maxAgeInSecond)
    {
        CookieDateFormat formatter = new CookieDateFormat();
        String expiredDate = formatter.format(new Date(System.currentTimeMillis() + maxAgeInSecond * 1000L));
        return expiredDate;
    }

    public static String toCookieStr(String key, String value, int maxAgeInSecond, String domain, String path, boolean isSecure, boolean isHttpOnly)
    {
        StringBuffer buf = new StringBuffer();
        buf.append(key).append("=").append(value);
        if(maxAgeInSecond >=0 )
        {
            buf.append("; ").append("expires=").append(getExpiredTime(maxAgeInSecond));
        }
        if(domain!=null && !domain.equals(""))
        {
            buf.append("; ").append("domain=").append(domain);
        }
        if(path!=null && !path.equals(""))
        {
            buf.append("; ").append("path=").append(path);
        }
        //
        if(isHttpOnly)
        {
            buf.append("; ").append("httpOnly");
        }
        //the cookie with a secure flag will only be sent over an HTTP
        if(isSecure)
        {
            buf.append("; ").append("secure");
        }

        return buf.toString();
    }

    public static String readCookie(String cookieStr, String name) {
        if (StringUtils.isBlank(cookieStr)) {
            return "";
        }

        String nameEQ = name + "=";
        String[] cks  = cookieStr.split(";");
        for(String ck: cks) {
            ck = ck.trim();
            Pattern p = Pattern.compile(nameEQ+"(.*)");
            Matcher m = p.matcher(ck);
            if (m.matches()) {
                return m.group(1);
            }
        }

        return "";
    }

    public static TreeMap<Long, String> getKwsFromJson(String jsonKws) {
        Gson gson = new Gson();

        TypeToken<TreeMap<Long, String>> mapTypeToken =
                new TypeToken<TreeMap<Long, String>>(){};

        TreeMap<Long, String> kwsMap = null;
        try {
            kwsMap = gson.fromJson(jsonKws, mapTypeToken.getType());
        } catch (Exception ex) {
            LOGGER.error("get keywords from json failed. jsonStr=" + jsonKws, ex);
        }

        if (kwsMap == null) {
            kwsMap = new TreeMap<Long, String>();
        }

        return kwsMap;
    }

    public static TreeMap<Long, String> jsonToTreeMap(JsonElement joKws) {
        if (joKws == null) {
            return null;
        }

        Gson gson = new Gson();

        TypeToken<TreeMap<Long, String>> mapTypeToken =
                new TypeToken<TreeMap<Long, String>>(){};

        TreeMap<Long, String> kwsMap = null;
        try {
            kwsMap = gson.fromJson(joKws, mapTypeToken.getType());
        } catch (Exception ex) {
            LOGGER.error("Convert json to TreeMap failed. joKws=" + joKws.toString(), ex);
        }

        return kwsMap;
    }

    public static String kwsToJson(TreeMap<Long, String> kwsMap) {
        return new Gson().toJson(kwsMap);
    }

    public static JsonObject putKeyword (JsonObject joInfo, String keyword) {
        if (!joInfo.has("kws")) {
            joInfo.add("kws", new JsonObject());
        }

        JsonObject joKws;
        try {
            joKws = joInfo.getAsJsonObject("kws");
        } catch (Exception ex) {
            LOGGER.error("Can not find element 'kws' in json: " + joInfo.toString(), ex);
            return joInfo;
        }

        Long timestamp = new DateTime(DateTimeZone.UTC).getMillis();
        joKws.addProperty(timestamp.toString(), keyword);

        return joInfo;
    }

    public static JsonObject putProfile (JsonObject joInfo, String profileSrc, String profileId) {
        if (!joInfo.has("profile")) {
            joInfo.add("profile", new JsonObject());
        }

        JsonObject joProfile;
        try {
            joProfile = joInfo.getAsJsonObject("profile");
        } catch (Exception ex) {
            LOGGER.error("Can not find element 'joProfile' in json: " + joInfo.toString(), ex);
            return joInfo;
        }

        joProfile.addProperty(profileSrc, profileId);

        return joInfo;
    }

    public static String getLastKeyword(String cookie) {
        String info = CookieUtils.readCookie(cookie, "info");

        if (StringUtils.isBlank(info)) {
            return "";
        }

        JsonObject joInfo = null;
        try {
            joInfo = new JsonParser().parse(info).getAsJsonObject();
        } catch (Exception ex) {
            LOGGER.error("Parse json from cookie failed. info = " + info, ex);
        }

        if (joInfo != null && joInfo.has("kws")) {
            JsonObject joKws = joInfo.getAsJsonObject("kws");
            TreeMap<Long, String> map = jsonToTreeMap(joKws);
            if (map != null) {
                return map.lastEntry().getValue();
            }
        }

        return "";
    }
}
