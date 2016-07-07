package com.cf.util;

import com.cf.util.model.Product;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;

/**
 * Created by ray on 6/13/16.
 */
public class ObjectTransformerTest {

    @Test
    public void testConvert() throws Exception{
        Map<String, String> fieldInfoMap = new HashMap<>();
        fieldInfoMap.put("sharedCount", "1K");
        fieldInfoMap.put("prime", "abslejlrjewlrjwelrwe");
        fieldInfoMap.put("title", "great title");
        fieldInfoMap.put("price", "56.67");
        List<Map<String, String>> products = new ArrayList<Map<String, String>>();
        products.add(fieldInfoMap);
        List<Product> productList = ObjectTransformer.convert(Product.class, products);
        assertEquals("Should be just 1 product there", 1, productList.size());
        assertEquals("Not right in share count", 1000, productList.get(0).getSharedCount().intValue());
        assertEquals("Not right in price", new Float(56.67), productList.get(0).getPrice());
        assertEquals("Not right in prime flag", true, productList.get(0).getPrime().booleanValue());
        assertEquals("Not right in title", "great title", productList.get(0).getTitle());

    }

}
