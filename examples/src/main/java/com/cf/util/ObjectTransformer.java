package com.cf.util;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.BeanUtils;


import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ray on 4/22/16.
 */
public class ObjectTransformer {

    private final static Logger LOGGER = Logger.getLogger(ObjectTransformer.class);

    /**
     * Convert parsed field map to the target object with the type clazz
     * @param clazz class you want to map to
     * @param resultMapList field map
     * @return List of target class
     */
    public static <T> List<T> convert(Class<T> clazz, List<Map<String, String>> resultMapList)
    {
        List<T> objList = new ArrayList<T>();
        ObjectMapper mapper = new ObjectMapper();

        for (Map<String, String> resultMap: resultMapList) {
            Map<String, Object> modifieldResultMap = new HashMap<>();

            for(String fieldName : resultMap.keySet()) {

                String resultValue = resultMap.get(fieldName);

                if(resultValue!=null && !resultValue.equals("")) {
                    PropertyDescriptor propertyDesc = BeanUtils.getPropertyDescriptor(clazz, fieldName);
                    if(propertyDesc !=null) {
                        if (propertyDesc.getPropertyType() == Boolean.class) {
                            if (resultValue.equalsIgnoreCase("false") ||
                                    resultValue.equalsIgnoreCase("f") ||
                                    resultValue.equalsIgnoreCase("n") ||
                                    resultValue.equalsIgnoreCase("no")) {
                                modifieldResultMap.put(fieldName, Boolean.valueOf("false"));
                            } else {
                                //any value will be true (means existed)
                                modifieldResultMap.put(fieldName, Boolean.valueOf("true"));
                            }
                        } else if (propertyDesc.getPropertyType() == Float.class ||
                                propertyDesc.getPropertyType() == Integer.class ||
                                propertyDesc.getPropertyType() == Double.class) {

                            resultValue = NumberUtil.formatNumber(resultValue);
                            modifieldResultMap.put(fieldName, resultValue);
                        } else {
                            modifieldResultMap.put(fieldName, resultValue);
                        }
                    }
                    else{
                        LOGGER.warn("Field name ["+fieldName+"] doesn't have the proper getter in the model ["+clazz.getName()+"]");
                    }
                }
            }
            objList.add(mapper.convertValue(modifieldResultMap, clazz));
        }
        return objList;
    }

    public static Class getFieldType(Class c, String fieldName) throws NoSuchFieldException
    {
        Field f = FieldUtils.getField(c, fieldName, true);
        return f.getType();
    }


}
