package org.xiaoxingbomei.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * http request 工具类
 * 1、json，通过key获取value
 * 2、
 */
public class Request_Utils
{
    // 1、json，通过key获取value
    public static String getParam(String paramJson,String key)
    {
        if(StringUtils.isEmpty(paramJson))
        {
            return null;
        }
        if(StringUtils.isNotEmpty(key))
        {
            Map<String,Object> reqMap = getParamMap(paramJson);
            if(reqMap.get(key) != null)
            {
                return String.valueOf(reqMap.get(key));
            }
        }
        return null;
    }

    //
    private static Map getParamMap(String paramJson)
    {
        if(StringUtils.isEmpty(paramJson))
        {
            return null;
        }
        return JSON.parseObject(paramJson,Map.class);
    }
}
