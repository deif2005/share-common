package com.usi.redis;

/**
 * RedisConstants
 * redis相关常量类
 * @date 2016/7/25
 */
public class RedisConstants {

    /**
     * 公共url资源key
     */
    public static final String PUBLIC_URL_KEY = "AUTH_PUBLIC_URL:%s";

    /**
     * 用户url资源key
     */
    public static final String USER_URL_KEY = "AUTH_USER_URL;USER_ID:%s,URL:%s";

    /**
     * 用户菜单资源key
     */
    public static final String USER_MENU_KEY = "AUTH_USER_MENU;USER_ID:%s";


    /***************************************************************************/

    /**
     * api口令时长
     * 时间秒
     */
    public static final long API_TOKEN_TIMEOUT = 7200;

    /**
     * api访问口令
     */
    public static final String API_ACCESS_TOKEN = "API_ACCESS_TOKEN";

    /**
     * 验证码有效时间
     */
    public static final long IDENTIFYINGCODE_TIMEOUT = 300;

    /**
     * 验证码key值名
     */
  //  public static final String IDENTIFYING_CODE ="IDENTIFYING_CODE:%s";

    /**
     * 验证码key值名
     */
    public static final String IDENTIFYING_CODE ="IDENTIFYING_CODE;PHONE_NUM:%s,PLATFORM_ID:%s,OPERATION_ID:%s";

}
