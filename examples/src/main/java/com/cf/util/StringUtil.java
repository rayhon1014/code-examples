package com.cf.util;

import com.google.common.base.Splitter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.log4j.Logger;
import org.stringtemplate.v4.ST;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: raymond
 * Date: 2/27/14
 * Time: 3:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class StringUtil {

    private static Logger LOGGER = Logger.getLogger(StringUtil.class);


    public static List<String> renderAll(List<String> templates, Map<String, String> fields)
    {
        List<String> messages = new ArrayList<String>();
        for(String template : templates){
            ST strTemplate = new ST(template);
            for(String key : fields.keySet())
            {
                strTemplate.add(key, fields.get(key));
            }
            messages.add(strTemplate.render());
        }
        return messages;
    }

    public static String stringify(List<NameValuePair> list) {
        StringBuilder b = new StringBuilder();
        b.append('{');
        boolean first = true;

        for (Iterator<NameValuePair> iterator = list.iterator(); iterator.hasNext(); ) {
            if (first)
                first = false;
            else
                b.append(",");

            NameValuePair next = iterator.next();
            b.append("\"" + next.getName() + "\"");
            b.append(':');
            b.append("\"" + next.getValue() + "\"");

        }

        b.append('}');
        return b.toString();
    }

    public static Map<String, String> splitToMap(String in, String delimiter) {
        if(delimiter==null || delimiter.equals(""))
        {
            delimiter = "|";
        }
        return Splitter.on(delimiter).trimResults().omitEmptyStrings().withKeyValueSeparator("=").split(in);
    }

    private static String matchesPattern(Pattern p, String inputStr) {
        Matcher m = p.matcher(inputStr);

        if (m.find()) {
            return m.group();
        }

        return null;
    }

    private static String containsWord(Set<String> words,String sentence) {
        for (String word : words) {
            if (sentence.contains(word)) {
                return word;
            }
        }

        return null;
    }

    public static int calculateStringSimilarity(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        // i == 0
        int [] costs = new int [b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }

	private static DecimalFormat currencyFormat = new DecimalFormat("#.##");
	public static String formatCurrency(Float num){
		return num == null? null : currencyFormat.format(num);
	}

	public static String stringify(Collection<String> values, String delimiter){
		return CollectionUtils.isEmpty(values) ? null : StringUtils.join(values, delimiter);
	}

	public static Float parseCurrency(String num){
		return StringUtils.isBlank(num) ? null : Float.parseFloat(num);
	}

	public static List<String> parseDelimitedString(String str, String delimiter){
		return StringUtils.isBlank(str) ? null : new ArrayList<String>(Arrays.asList(str.split(delimiter)));
	}

	public static Float parseFloat(String num){
		return StringUtils.isBlank(num) ? null : Float.parseFloat(num);
	}

	public static String formatFloat(Float num){
		return num == null? null : num.toString();
	}

	/**
	 * Redis does not allow null as value of the key, so we need to remove it before calling redis operation
	 * @param valueMap
	 */
	public static void cleanUpKeyWithNullValue(Map<String, String> valueMap){
		List<String> nullValueKeys = new ArrayList<String>();

		for(String key : valueMap.keySet()){
			if(valueMap.get(key) == null){
				nullValueKeys.add(key);
			}
		}

		for(String key : nullValueKeys) {
			valueMap.remove(key);
		}
	}

    public static String md5(String input) throws NoSuchAlgorithmException {
        MessageDigest m = MessageDigest.getInstance("MD5");
        m.update(input.getBytes(), 0, input.length());
        return new BigInteger(1,m.digest()).toString(16);
    }
}
