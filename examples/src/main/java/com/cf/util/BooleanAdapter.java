package com.cf.util;

/**
 * Created by IntelliJ IDEA.
 * User: raymond
 * Date: 12/19/12
 * Time: 2:55 PM
 * To change this template use File | Settings | File Templates.
 */
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class BooleanAdapter extends XmlAdapter<Integer, Boolean>
{
    @Override
    public Boolean unmarshal( Integer s )
    {
        return s == null ? null : s == 1;
    }

    @Override
    public Integer marshal( Boolean c )
    {
        return c == null ? null : c ? 1 : 0;
    }
}
