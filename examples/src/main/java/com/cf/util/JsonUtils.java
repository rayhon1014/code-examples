package com.cf.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: raymond
 * Date: 5/20/14
 * Time: 2:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class JsonUtils {

    private static final Logger LOGGER = Logger.getLogger(JsonUtils.class);

    public static Map<String, String> jsonToMap(String json) {
        Map<String, String> mapParams = null;

        try {
            TypeToken<HashMap<String, String>> mapTypeToken =
                    new TypeToken<HashMap<String, String>>(){};
            mapParams = new Gson().fromJson(json,  mapTypeToken.getType());
        } catch (Exception ex) {
            LOGGER.error("Parse json to map failed. json=" + json, ex);
        }

        return mapParams;
    }

}
