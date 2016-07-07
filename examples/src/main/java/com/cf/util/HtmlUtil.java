package com.cf.util;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ray on 4/25/16.
 */
public class HtmlUtil {
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(HtmlUtil.class);

    public static String format(String html)
    {

        long start = System.currentTimeMillis();
        TagNode tagNode = new HtmlCleaner().clean(html);
        LOGGER.info("html clean ["+(System.currentTimeMillis() - start)+"] ms");
        start = System.currentTimeMillis();
        String cleanHtml =  new PrettyXmlSerializer(new CleanerProperties()).getAsString(tagNode);
        LOGGER.info("html prettified ["+(System.currentTimeMillis() - start)+"] ms");
        return cleanHtml;
    }

    /**
     * spec with key: field name, value: jsoup css query down to attribute level
     * if you want to get the img element src value, you just need to do img/@src
     * if we don't see /@[attr], we will assume the element content is the content you want
     *
     * if the value path you put has > 1 elements, we use | as separator to concat the values
     * ref: https://jsoup.org/apidocs/org/jsoup/select/Selector.html
     *
     *
     * @param spec
     * @return
     */
    public static Map<String, String> extractAttributeValues(Element htmlElement, Map<String, String> spec)
    {
        Map<String, String> result = new HashMap<>();
        for(Map.Entry entry : spec.entrySet()) {
            String attributeKey = entry.getKey().toString();
            String attributePath = entry.getValue().toString();
            String value = "";
            String[] attributeNames = null;
            String[] attributePaths = attributePath.split(",");
            for(String path : attributePaths)
            {
                path = path.trim();
                if(path.indexOf("/@") > -1) {
                    attributeNames = path.substring(path.lastIndexOf("/@") + 2).split("\\|");
                    path = path.substring(0, path.lastIndexOf("/@"));
                }

                //first one win policy if you have a list of possible paths for a field
                if(result.get(attributeKey)==null || result.get(attributeKey).equals("")) {
                    Elements elements = htmlElement.select(path);

                    for (int i = 0; i < elements.size(); i++) {
                        Element element = elements.get(i);
                        String elementValue = "";
                        if (attributeNames != null) {
                            for (String attributeName : attributeNames) {
                                elementValue = element.attr(attributeName);
                                if (elementValue != null && !elementValue.equals("")) {
                                    break;
                                }
                            }

                        } else {
                            elementValue = element.text();
                        }
                        value += elementValue;
                        if (elements.size() > 1 && i < elements.size() - 1) {
                            if (elementValue != null && !elementValue.trim().equals("")) {
                                value += "|";
                            }
                        }
                    }
                    result.put(attributeKey, value);
                }
            }

        }
        return result;
    }



    public static boolean isHtml(String content)
    {
        if(!content.isEmpty() && content.toLowerCase().indexOf("html")>-1)
        {
            return true;
        }
        return false;
    }


}
