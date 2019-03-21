package com.wd.token;

import com.wd.componetutil.SpringUtil;
import com.wd.redis.IRedisRepository;
import com.wd.redis.RedisConstants;
import com.wd.util.UUIDGenerator;
import com.wd.util.VertifyCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author miou
 * @date 2018/7/16
 */
public class TokenUtil {

    private static Logger logger = LoggerFactory.getLogger(TokenUtil.class);

    static IRedisRepository redisRepository;

    static{
        redisRepository = SpringUtil.getBean(IRedisRepository.class);
    }

    /**
     * 设置api访问口令，限时120分钟
     * @param token
     */
    private static void setApiAccessToken(String token){
        redisRepository.set(RedisConstants.API_ACCESS_TOKEN,token,RedisConstants.API_TOKEN_TIMEOUT, TimeUnit.SECONDS);
    }

    /**
     * 获取api访问口令，如果不存在即生成
     * @return
     */
    public static String getApiAccessToken(){
        String token = "";
        try {
            token = UUIDGenerator.getUUID();
            if (!redisRepository.exists(RedisConstants.API_ACCESS_TOKEN)){
                setApiAccessToken(token);
                return token;
            }else{
                token = (String)redisRepository.get(RedisConstants.API_ACCESS_TOKEN);
            }
        }catch (Exception e){
            logger.error("获取api访问Token失败",e);
        }
        return token;
    }

    /**
     * token值校验
     * @param token
     * @return
     */
    public static boolean verifyingToken(String token){
        boolean result = false;
        String validToken = (String)redisRepository.get(RedisConstants.API_ACCESS_TOKEN);
        if (token.equals(validToken)){
            result = true;
        }
        return result;
    }

    /**
     * 生成随机验证码,存入缓存,五分钟内有效
     * @param phoneNum 电话
     * @param platformId
     * @param operationId 1:注册;2:忘记密码
     * @return string
     */
    public static String createIdentifyingCode(String phoneNum,String platformId,String operationId){
        String keyStr = String.format(RedisConstants.IDENTIFYING_CODE, phoneNum ,platformId,
                operationId);
        if (redisRepository.exists(keyStr)){
            redisRepository.del(keyStr);
        }
        String randomStr = VertifyCodeUtil.getRandromNum();
        redisRepository.set(keyStr,randomStr, RedisConstants.IDENTIFYINGCODE_TIMEOUT,TimeUnit.SECONDS);
        return randomStr;
    }

    /**
     * 验证码校验
     * @param phoneNum
     * @param identCode
     * @param platformId 平台id
     * @param operationId 1:注册;2:忘记密码
     * @return
     */
    public static boolean verifyingIdentCode(String phoneNum,String platformId, String operationId, String identCode){
        boolean result = false;
        if (!redisRepository.exists(String.format(RedisConstants.IDENTIFYING_CODE,phoneNum,platformId,operationId))){
            return result;
        }
        String validIdentCode = (String)redisRepository.get(String.format(RedisConstants.IDENTIFYING_CODE,phoneNum,platformId,operationId));

        if (identCode.equals(validIdentCode)){
            result = true;
        }
        return result;
    }
}
