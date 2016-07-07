package com.cf.util;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

public class CurrencyConverter {
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger
            .getLogger(CurrencyConverter.class);
    private static Map<String, String> currencyMap;

    static {
        currencyMap = new HashedMap();
        currencyMap.put("$", "USD");
    }

    public static String getCurrencyInDoller(String currency, String type) {
        try {
            if (currencyMap.containsKey(type)) {
                type = currencyMap.get(type);
            }
            URL url = new URL("http://www.google.com/ig/calculator?hl=en&q=" + currency + type + "=?USD");
            String content = IOUtils.toString(new InputStreamReader(
                    url.openStream()));
            JSONObject json = new JSONObject(content);
            if (json.has("rhs")) {
                String value = json.get("rhs").toString();
                if (value.contains("U.S.")) {
                    value = value.substring(0, value.indexOf("U.S."));

                    value = value.replaceAll(",", "");
                    if (value.contains("million")) {
                        double ddollar = Double.parseDouble((value.replaceAll("million", "")).trim());
                        ddollar = ddollar * 1000000;
                        value = String.valueOf(ddollar);
                    }
                    return value;
                }
            }
        } catch (Exception e) {
            LOGGER.info(e);
        }
        return null;
    }

    public static void main(String arg[]) {
        //CurrencyConverter cc= new CurrencyConverter();
        //LOGGER.info("Value: "+cc.getCurrencyInDoller("100", "AUD"));
        String str = "$";
        if (str.contains("$")) {
            LOGGER.info(true);
        } else if (str.equals("$")) {
            LOGGER.info("equal");
        }


    }
}
