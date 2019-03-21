package com.wd.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class HttpInvoke {
    static private Logger logger = LoggerFactory.getLogger(HttpInvoke.class);
    private static String CHARACTER_ENCODING = "utf-8";
    private static String LINE_SEPARATOR = System.getProperty("line.separator",
            "\n");
    private static HttpInvoke HTTPINVOKE = new HttpInvoke();

    public static HttpInvoke getInstance() {
        return HTTPINVOKE;
    }

    private HttpInvoke() {
        super();
    }

    public String doGet(String url) {
        logger.debug("have a get invoke:" + url);
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuffer result = new StringBuffer();
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setUseCaches(false);
            //connection.setRequestProperty("User-Agent", " Mozilla/5.0 Ubuntu");
            //	connection.setRequestProperty("Accept", " */*");
            connection.connect();
            reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String readline = null;
            while ((readline = reader.readLine()) != null) {
                result.append(readline).append(LINE_SEPARATOR);
            }
            if (result.length() > 0) {
                result.delete(result.lastIndexOf(LINE_SEPARATOR),
                        result.length());
            }
        } catch (MalformedURLException e) {
            logger.error("URL格式不正确.url" + url, e);
        } catch (IOException e) {
            logger.error("出现IO错误.url" + url, e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            dispose(reader);
        }
        logger.debug("get result:"+result.toString());
        return result.toString();
    }

    public String doGet(String url, Map<String, String> params) {
        StringBuffer result = new StringBuffer(url);
        StringBuffer sb = queryString(params, true);
        if (url.indexOf('?') > -1) {
            if (url.endsWith("?")) {
                result.append(sb);
            } else {
                sb.insert(0, '&');
                result.append(sb);
            }
        } else {
            result.append('?').append(sb);
        }
        return doGet(result.toString());
    }

    private StringBuffer queryString(Map<String, String> params, boolean encode) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> item : params.entrySet()) {
            try {
                sb.append(item.getKey())
                        .append('=')
                        .append(encode ? URLEncoder.encode(item.getValue(),
                                CHARACTER_ENCODING) : item.getValue())
                        .append('&');
            } catch (UnsupportedEncodingException e) {
                logger.error("编码错误: params" + params, e);
                throw new RuntimeException(e);
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb;
    }

    public String doPost(String url, Map<String, String> params) {
        String query=queryString(params, false).toString();
        logger.debug("have a post invoke:" + url+"\tquery=["+query+"]");
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;
        StringBuffer result = new StringBuffer();
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setUseCaches(false);
            //connection.setRequestProperty("User-Agent", " Mozilla/5.0 Ubuntu");
            //connection.setRequestProperty("Accept", " */*");
            connection.setDoOutput(true);
            connection.connect();
            writer = new BufferedWriter(new OutputStreamWriter(
                    connection.getOutputStream()));
            writer.write(query);
            writer.flush();
            reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String readline = null;
            while ((readline = reader.readLine()) != null) {
                result.append(readline).append(LINE_SEPARATOR);
            }
            if (result.length() > 0) {
                result.delete(result.lastIndexOf(LINE_SEPARATOR),
                        result.length());
            }
        } catch (MalformedURLException e) {
            logger.error("URL格式不正确.url" + url, e);
        } catch (IOException e) {
            logger.error("出现IO错误.url" + url, e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            dispose(reader);
            dispose(writer);
        }
        logger.debug("get result:"+result.toString());
        return result.toString();
    }

    private void dispose(Reader reader) {
        try {
            reader.close();
        } catch (IOException e) {
            logger.error("reader关闭IO出错", e);
        }
    }

    private void dispose(Writer writer) {
        try {
            writer.close();
        } catch (IOException e) {
            logger.error("writer关闭IO出错", e);
        }
    }
}
