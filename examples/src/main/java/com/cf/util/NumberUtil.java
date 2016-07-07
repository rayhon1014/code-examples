package com.cf.util;

/**
 * Created by ray on 6/14/16.
 */
public class NumberUtil {

    /**
     * 1.2k => 1200
     * 1.2m => 1200000
     * 0.2 k=> 200
     * @return
     */
    public static String formatNumber(String resultValue)
    {
        int multiplyFactor = 1;

        resultValue = resultValue.toLowerCase();
        if(resultValue.indexOf("k") > -1) {
            multiplyFactor = 1000;
            resultValue = resultValue.replaceAll("k", "").trim();
        }
        else if(resultValue.indexOf("m") > -1)
        {
            multiplyFactor = 1000000;
            resultValue = resultValue.replaceAll("m", "").trim();
        }
        if(multiplyFactor > 1) {
            Double number = new Double(resultValue);
            return ((int) (number.doubleValue() * multiplyFactor)) + "";
        }
        else{
            return resultValue;
        }
    }
}
