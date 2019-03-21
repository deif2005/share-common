/**
 * 微信公众平台开发模式(JAVA) SDK
 * (c) 2012-2013 ____′↘夏悸 <wmails@126.cn>, MIT Licensed
 * http://www.jeasyuicn.com/wechat
 */
package com.wd.util;


import com.alibaba.fastjson.JSONObject;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Response;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

//import com.wd.microschoolappsvr.domain.common.Attachment;

/**
 * https 请求 微信为https的请求
 *
 * @author L.cm
 * @date 2013-10-9 下午2:40:19
 */ 
public class HttpKit {
	private static final String DEFAULT_CHARSET = "UTF-8";
    /**
     * @return 返回类型:
     * @throws IOException
     * @throws UnsupportedEncodingException
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @description 功能描述: get 请求
     */
	
	private static AsyncHttpClientConfig config = new AsyncHttpClientConfig.Builder().setConnectionTimeoutInMs(30*1000).setAllowPoolingConnection(true).build();
	
    public static String get(String url, Map<String, String> params, Map<String, String> headers) throws IOException, ExecutionException, InterruptedException {
        AsyncHttpClient http = new AsyncHttpClient(config);
        AsyncHttpClient.BoundRequestBuilder builder = http.prepareGet(url);
        builder.setBodyEncoding(DEFAULT_CHARSET);
        if (params != null && !params.isEmpty()) {
            Set<String> keys = params.keySet();
            for (String key : keys) {
                builder.addQueryParameter(key, params.get(key));
            }
        }

        if (headers != null && !headers.isEmpty()) {
            Set<String> keys = headers.keySet();
            for (String key : keys) {
                builder.addHeader(key, headers.get(key));
            }
        }
        Future<Response> f = builder.execute();
        String body = f.get().getResponseBody(DEFAULT_CHARSET);
        http.close();
        return body;
    }
    
    
    public static String getWithEncoding(String url, String encoding) throws IOException, ExecutionException, InterruptedException {
        AsyncHttpClient http = new AsyncHttpClient();
        AsyncHttpClient.BoundRequestBuilder builder = http.prepareGet(url);
        builder.setBodyEncoding(DEFAULT_CHARSET);
        Future<Response> f = builder.execute();
        String body = f.get().getResponseBody(encoding);
        http.close();
        return body;
    }

    /**
     * @return 返回类型:
     * @throws IOException
     * @throws UnsupportedEncodingException
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @description 功能描述: get 请求
     */
    public static String get(String url) throws Exception {
        return get(url, null);
    }

    /**
     * @return 返回类型:
     * @description 功能描述: get 请求
     */
    public static String get(String url, Map<String, String> params) throws IOException, ExecutionException, InterruptedException {
        return get(url, params, null);
    }
    
    /**
     * @return 返回类型:
     * @throws IOException
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @description 功能描述: POST 请求
     */
    public static String post(String url, Map<String, String> params, Map<String, String> header) throws IOException, ExecutionException, InterruptedException {
        AsyncHttpClient http = new AsyncHttpClient();
        AsyncHttpClient.BoundRequestBuilder builder = http.preparePost(url);
        builder.setBodyEncoding(DEFAULT_CHARSET);
        if (params != null && !params.isEmpty()) {
            Set<String> keys = params.keySet();
            for (String key : keys) {
                builder.addParameter(key, params.get(key));
            }
        }
        Set<String> keys = header.keySet();
        for (String key : keys) {
            builder.setHeader(key, header.get(key));
        }
        
        Future<Response> f = builder.execute();
        String body = f.get().getResponseBody(DEFAULT_CHARSET);
        http.close();
        return body;
    }

    /**
     * @return 返回类型:
     * @throws IOException
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @description 功能描述: POST 请求
     */
    public static String post(String url, Map<String, String> params) throws IOException, ExecutionException, InterruptedException {
        AsyncHttpClient http = new AsyncHttpClient();
        AsyncHttpClient.BoundRequestBuilder builder = http.preparePost(url);
        builder.setBodyEncoding(DEFAULT_CHARSET);
        if (params != null && !params.isEmpty()) {
            Set<String> keys = params.keySet();
            for (String key : keys) {
                builder.addParameter(key, params.get(key));
            }
        }
        Future<Response> f = builder.execute();
        String body = f.get().getResponseBody(DEFAULT_CHARSET);
        http.close();
        return body;
    }

    /**
     * 上传媒体文件
     *
     * @param url
     * @param file
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws KeyManagementException
     */
    public static String upload(String url, File file) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException, ExecutionException, InterruptedException {
        AsyncHttpClient http = new AsyncHttpClient();
        AsyncHttpClient.BoundRequestBuilder builder = http.preparePost(url);
        builder.setBodyEncoding(DEFAULT_CHARSET);
        String BOUNDARY = "----WebKitFormBoundaryiDGnV9zdZA1eM1yL"; // 定义数据分隔线
        builder.setHeader("connection", "Keep-Alive");
        builder.setHeader("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.107 Safari/537.36");
        builder.setHeader("Charsert", "UTF-8");
        builder.setHeader("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
        byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();// 定义最后数据分隔线
        builder.setBody(new UploadEntityWriter(end_data, file));

        Future<Response> f = builder.execute();
        String body = f.get().getResponseBody(DEFAULT_CHARSET);
        http.close();
        return body;
    }

    /**
     * 下载资源
     *
     * @param url
     * @return
     * @throws IOException
     */
//    public static Attachment download(String url) throws ExecutionException, InterruptedException, IOException {
//        Attachment att = new Attachment();
//        AsyncHttpClient http = new AsyncHttpClient();
//        AsyncHttpClient.BoundRequestBuilder builder = http.prepareGet(url);
//        builder.setBodyEncoding(DEFAULT_CHARSET);
//        Future<Response> f = builder.execute();
//        if (f.get().getContentType().equalsIgnoreCase("text/plain")) {
//            att.setError(f.get().getResponseBody(DEFAULT_CHARSET));
//        } else {
//            BufferedInputStream bis = new BufferedInputStream(f.get().getResponseBodyAsStream());
//            String ds = f.get().getHeader("Content-disposition");
//            String fullName = ds.substring(ds.indexOf("filename=\"") + 10, ds.length() - 1);
//            String relName = fullName.substring(0, fullName.lastIndexOf("."));
//            String suffix = fullName.substring(relName.length() + 1);
//
//            att.setFullName(fullName);
//            att.setFileName(relName);
//            att.setSuffix(suffix);
//            att.setContentLength(f.get().getHeader("Content-Length"));
//            att.setContentType(f.get().getContentType());
//            att.setFileStream(bis);
//        }
//        http.close();
//        return att;
//    }

    public static String post(String url, String s) throws IOException, ExecutionException, InterruptedException {
        AsyncHttpClient http = new AsyncHttpClient();
        AsyncHttpClient.BoundRequestBuilder builder = http.preparePost(url);
        builder.setBodyEncoding(DEFAULT_CHARSET);
        builder.setBody(s);
        Future<Response> f = builder.execute();
        String body = f.get().getResponseBody(DEFAULT_CHARSET);
        http.close();
        return body;
    }

    public static String postJson(String url, String s) throws IOException, ExecutionException, InterruptedException {
        AsyncHttpClient http = new AsyncHttpClient();
        AsyncHttpClient.BoundRequestBuilder builder = http.preparePost(url);

        String BOUNDARY = "----WebKitFormBoundaryiDGnV9zdZA1eM1yL"; // 定义数据分隔线
        builder.setHeader("connection", "Keep-Alive");
        builder.setHeader("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.107 Safari/537.36");
        builder.setHeader("Charsert", "UTF-8");
        builder.setHeader("Content-Type", "application/json");
        byte[] end_data =s.getBytes();// 定义最后数据分隔线
        builder.setBody(end_data);

        Future<Response> f = builder.execute();
        String body = f.get().getResponseBody(DEFAULT_CHARSET);
        http.close();
        return body;
    }
    
    public static String postWithSecurity(String url, SSLConnectionSocketFactory sslsf, String s) throws IOException, ExecutionException, InterruptedException {
    	CloseableHttpClient http = HttpClients.custom().setSSLSocketFactory(sslsf).build();
    	HttpPost httppost = new HttpPost(url);
    	StringBuilder sb = new StringBuilder();
    	try {

    		StringEntity reqEntity = new StringEntity(s);
    		reqEntity.setContentType("application/x-www-form-urlencoded"); 
			httppost.setEntity(reqEntity);
            System.out.println("executing request" + httppost.getRequestLine());

            CloseableHttpResponse response = http.execute(httppost);
            try {
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent(), "utf-8"));
                    String text;
                    while ((text = bufferedReader.readLine()) != null) {
                    	sb.append(text);
                    }
                }
                EntityUtils.consume(entity);
            } finally {
                response.close();
            }
        } finally {
        	http.close();
        }
    	return sb.toString();
    }
    
    public static void listImage(String token) throws IOException, ExecutionException, InterruptedException {
        Map<String,Object> json = new HashMap<String,Object>();
        json.put("type", "image");
        json.put("offset", 0);
        json.put("count", 100);
        String post = post("https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=" + token, JSONObject.toJSONString(json));
        System.out.println(post);
    }
    
    public static void listVideo(String token) throws IOException, ExecutionException, InterruptedException {
        Map<String,Object> json = new HashMap<String,Object>();
        json.put("type", "video");
        json.put("offset", 0);
        json.put("count", 100);
        String post = post("https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=" + token, JSONObject.toJSONString(json));
        System.out.println(post);
    }
    
    public static void exchangeVideoLink(String token, String originalId, String title, String desc) throws IOException, ExecutionException, InterruptedException {
        Map<String,Object> json = new HashMap<String,Object>();
        json.put("media_id", originalId);
        json.put("title", 0);
        json.put("description", desc);
        String post = post("https://api.weixin.qq.com/cgi-bin/media/uploadvideo?access_token=" + token, JSONObject.toJSONString(json));
        System.out.println(post);
    }
    
    public  static void main(String[] args) throws Exception {
        String token = "IhZMzG5-CjvRsnBN6WSUXmRWxRK2cbWPX84Kl7vB62LuLOz61RC7xFNZwDU7ydr24EEydVxnD1cwAWNlzxzAnCtG2wkHrd_cTr6vlhTUtWzlXg9nsn2NfXRoePPeFksaQSVgAAAZBE";
        listImage(token);
    }
}