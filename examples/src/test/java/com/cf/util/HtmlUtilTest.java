package com.cf.util;

import static org.junit.Assert.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;
import us.codecraft.xsoup.Xsoup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ray on 4/30/16.
 */
public class HtmlUtilTest {

    @Test
    public void testExtractAttributeValue()
    {
        String xml = "<?xml version=\"1.0\"?>\n" +
                "<?xml-stylesheet href=\"catalog.xsl\" type=\"text/xsl\"?>\n" +
                "<!DOCTYPE catalog SYSTEM \"catalog.dtd\">\n" +
                "<catalog>\n" +
                "   <product description=\"Cardigan Sweater\" product_image=\"cardigan.jpg\">\n" +
                "      <catalog_item gender=\"Men's\">\n" +
                "         <item_number>QWZ5671</item_number>\n" +
                "         <price>39.95</price>\n" +
                "         <size description=\"Medium\">\n" +
                "            <color_swatch image=\"red_cardigan.jpg\">Red</color_swatch>\n" +
                "            <color_swatch image=\"burgundy_cardigan.jpg\">Burgundy</color_swatch>\n" +
                "         </size>\n" +
                "      </catalog_item>\n" +
                "      <catalog_item gender=\"Women's\">\n" +
                "         <item_number>RRX9856</item_number>\n" +
                "         <price>42.50</price>\n" +
                "         <size description=\"Small\">\n" +
                "            <color_swatch image=\"red_cardigan.jpg\">Red</color_swatch>\n" +
                "            <color_swatch image=\"navy_cardigan.jpg\">Navy</color_swatch>\n" +
                "            <color_swatch image=\"burgundy_cardigan.jpg\">Burgundy</color_swatch>\n" +
                "         </size>\n" +
                "         <size description=\"Medium\">\n" +
                "            <color_swatch image=\"red_cardigan.jpg\">Red</color_swatch>\n" +
                "            <color_swatch image=\"navy_cardigan.jpg\">Navy</color_swatch>\n" +
                "            <color_swatch image=\"burgundy_cardigan.jpg\">Burgundy</color_swatch>\n" +
                "            <color_swatch image=\"black_cardigan.jpg\">Black</color_swatch>\n" +
                "         </size>\n" +
                "         <size description=\"Large\">\n" +
                "            <color_swatch image=\"navy_cardigan.jpg\">Navy</color_swatch>\n" +
                "            <color_swatch image=\"black_cardigan.jpg\">Black</color_swatch>\n" +
                "         </size>\n" +
                "         <size description=\"Extra Large\">\n" +
                "            <color_swatch image=\"burgundy_cardigan.jpg\">Burgundy</color_swatch>\n" +
                "            <color_swatch image=\"black_cardigan.jpg\">Black</color_swatch>\n" +
                "         </size>\n" +
                "      </catalog_item>\n" +
                "   </product>\n" +
                "</catalog>";

        Document doc = Jsoup.parse(xml);
        Elements elements = doc.select("catalog_item");
        Map<String, String> spec = new HashMap<String, String>();
        spec.put("color", "size color_swatch");
        spec.put("size", "size[description]");
        Map<String, String> result = HtmlUtil.extractAttributeValues(elements.first(), spec);
        assertEquals(result.get("color"), "Red|Burgundy");
        assertEquals(result.get("size"), "Medium");
    }

}
