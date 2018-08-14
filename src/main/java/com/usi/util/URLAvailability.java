package com.usi.util;

import org.apache.commons.lang.StringUtils;

import java.net.HttpURLConnection;
import java.net.URL;

//import org.apache.log4j.Logger;

/**
 * URLAvailability
 * url验证工具类
 * @date 2016/5/24
 */
@SuppressWarnings("unused")
public class URLAvailability {

//    private static Logger logger = Logger.getLogger(URLAvailability.class);

    /**
     * 验证url是否可访问有效，有效则返回，无效则返回null
     * @param url url
     * @param tryCount 重试次数
     * @return
     */
    public static String isAvailability(String url,int tryCount) {
        if(StringUtils.isBlank(url)){
            return null;
        }
        int index = 0;
        while (index < tryCount) {
            try {
                URL urlStr = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlStr.openConnection();
                int state = connection.getResponseCode();
                if (state == 200) {
                    return url;
                }
                break;
            } catch (Exception ex) {
                index ++;
                continue;
            }
        }
        return null;
    }
}

