package com.usi.util;

import com.usi.encrypt.MD5Utils;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * HttpUtils
 * http工具类
 * @author yuhao
 * @date 2016/8/11
 */
public class HttpUtil {

    private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    /**
     * post请求
     * @param url url地址
     * @param json 参数,JSONObject/JSONArray
     * @return
     */
    public static String sentPost(String url,String json) {
        //post请求返回结果
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        String jsonResult = "";
        HttpPost method = new HttpPost(url);
        try {
            if (StringUtils.isNotBlank(json)) {
                //解决中文乱码问题
                StringEntity entity = new StringEntity(json.toString(), "utf-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/text");
                method.setEntity(entity);
            }
            logger.debug("-------HttpUtil工具类sentPost请求，请求地址：{}，请求参数：{}",new Object[]{url,json});
            HttpResponse result = httpClient.execute(method);
            url = URLDecoder.decode(url, "UTF-8");
            /**请求发送成功，并得到响应**/
            if (result.getStatusLine().getStatusCode() == 200) {
                String str;
                try {
                    /**读取服务器返回过来的json字符串数据**/
                    jsonResult = EntityUtils.toString(result.getEntity());
                    logger.debug("-------HttpUtil工具类sentPost请求，请求地址：{}，响应参数：{}",new Object[]{url,jsonResult});
                    /**把json字符串转换成json对象**/
                    //jsonResult = JSONObject.fromObject(str);
                } catch (Exception e) {
                    logger.error("post请求提交失败:" + url, e);
                }
            }
        } catch (IOException e) {
            logger.error("post请求提交失败:" + url, e);
        }
        return jsonResult;
    }



    /**
     * 发送get请求
     * @param url 路径
     * @return
     */
    public static String sendGet(String url){
        //get请求返回结果
        JSONObject jsonResult = null;
        try {
            CloseableHttpClient client = HttpClientBuilder.create().build();
            //发送get请求
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);

            /**请求发送成功，并得到响应**/
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                /**读取服务器返回过来的json字符串数据**/
                String strResult = EntityUtils.toString(response.getEntity());
                /**把json字符串转换成json对象**/
                jsonResult = JSONObject.fromObject(strResult);
                url = URLDecoder.decode(url, "UTF-8");
            } else {
                logger.error("get请求提交失败:" + url);
            }
        } catch (IOException e) {
            logger.error("get请求提交失败:" + url, e);
        }
        return jsonResult == null ? null : jsonResult.toString();
    }


    /**
     * post请求 form  表单提交
     * @param url url地址
     * @param pairList 参数
     * @return
     */
    public static String sentFormPost(String url, List<BasicNameValuePair> pairList ) {
        //post请求返回结果
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        String respContent = "";
        HttpPost method = new HttpPost(url);
        try {
            //解决中文乱码问题
            method.setEntity(new UrlEncodedFormEntity(pairList, "utf-8") );

            HttpResponse resp = httpClient.execute(method);

            url = URLDecoder.decode(url, "UTF-8");
            /**请求发送成功，并得到响应**/
            if(resp.getStatusLine().getStatusCode() == 200) {
                try {
                    /**读取服务器返回过来的json字符串数据**/
                    respContent = EntityUtils.toString(resp.getEntity(),"UTF-8");
                } catch (Exception e) {
                    logger.error("post请求提交失败:" + url, e);
                }
            }

        } catch (IOException e) {
            logger.error("post请求提交失败:" + url, e);
        }
        return respContent;
    }

    /**
     * 发送get请求 创壹平台专用
     * @param url 路径
     * @param date 数据
     * @return
     */
    public static String sendGetCy(String url,String date){
        //get请求返回结果
        JSONObject jsonResult = null;
        try {
            CloseableHttpClient client = HttpClientBuilder.create().build();
            //发送get请求
            date = URLEncoder.encode(date,"UTF-8");
            HttpDelete httpDelete = new HttpDelete(url+date);
            httpDelete.setHeader("Content-Type","application/x-www-form-urlencoded");
            httpDelete.setHeader("appId","1e0859a69c96d716f607f5aa");
            httpDelete.setHeader("appSecret","416634df3018ff8ba35ffb92");
            HttpResponse response = client.execute(httpDelete);

            /**请求发送成功，并得到响应**/
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                /**读取服务器返回过来的json字符串数据**/
                String strResult = EntityUtils.toString(response.getEntity());
                /**把json字符串转换成json对象**/
                jsonResult = JSONObject.fromObject(strResult);
                url = URLDecoder.decode(url, "UTF-8");
            } else {
                logger.error("get请求创壹平台失败:" + url);
            }
        } catch (IOException e) {
            logger.error("get请求创壹平台失败:" + url, e);
        }
        return jsonResult == null ? null : jsonResult.toString();
    }

    /**
     * post请求 创壹平台专用
     * @param url 请求地址
     * @param pairList  请求数据 json 格式
     * @param type  1，添加  2，修改
     * @return
     */
    public static String sentPostCy(String url, List<BasicNameValuePair> pairList, int type) {
        //post请求返回结果
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        String jsonResult = "";
        HttpPost method = null;
        HttpPut httpPut = null;
        HttpResponse result = null;
        try {
            //解决中文乱码问题
            if(type ==1){
                method = new HttpPost(url);
                method.setEntity(new UrlEncodedFormEntity(pairList, "utf-8") );
                method.setHeader("Content-Type","application/x-www-form-urlencoded");
                method.setHeader("appId","1e0859a69c96d716f607f5aa");
                method.setHeader("appSecret","416634df3018ff8ba35ffb92");
                result = httpClient.execute(method);
            }else{
                httpPut = new HttpPut(url);
                httpPut.setEntity(new UrlEncodedFormEntity(pairList, "utf-8") );
                httpPut.setHeader("Content-Type","application/x-www-form-urlencoded");
                httpPut.setHeader("appId","1e0859a69c96d716f607f5aa");
                httpPut.setHeader("appSecret","416634df3018ff8ba35ffb92");
                result = httpClient.execute(httpPut);
            }
            logger.debug("-------HttpUtil工具类sentPost请求，请求地址：{}，请求参数：{}",new Object[]{url,pairList});
            url = URLDecoder.decode(url, "UTF-8");
            /**请求发送成功，并得到响应**/
            if (result.getStatusLine().getStatusCode() == 200) {
                String str;
                try {
                    /**读取服务器返回过来的json字符串数据**/
                    jsonResult = EntityUtils.toString(result.getEntity());
                    logger.debug("-------HttpUtil工具类sentPost请求，请求地址：{}，响应参数：{}",new Object[]{url,jsonResult});
                    /**把json字符串转换成json对象**/
                    //jsonResult = JSONObject.fromObject(str);
                } catch (Exception e) {
                    logger.error("post请求创壹平台失败:" + url, e);
                }
            }
        } catch (IOException e) {
            logger.error("post请求创壹平台失败:" + url, e);
        }
        return jsonResult;
    }

    public static void main(String[] args) throws Exception {
        List<BasicNameValuePair> pairList = new ArrayList<>();
        //验签
        String str  = "captcha=142583&phone=15200300282";
        String md5Str = "a496c3d0d3aeba51fdcbdf73695fffff"+ MD5Utils.getMD5(str);
        String sign = StringUtil.getSignature(md5Str,"sa95c1c1329cd8eb533f69361e1167f2");


        pairList.add(new BasicNameValuePair("access_key","a496c3d0d3aeba51fdcbdf73695fffff"));
        pairList.add(new BasicNameValuePair("sign",sign));
        pairList.add(new BasicNameValuePair("phone","15200300282"));
        pairList.add(new BasicNameValuePair("captcha","142583"));
        HttpUtil.sentFormPost("http://passport.ac.enimo.cn/openapi/v1/account/userlogin",pairList);
    }
}