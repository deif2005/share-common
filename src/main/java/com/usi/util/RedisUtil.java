package com.wd.util;

import redis.clients.jedis.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 廖大剑
 * @version V1.0
 * @Description:  redis 工具类
 * @Company:
 * @date 2015/9/7
 */
public class RedisUtil {
    //服务端Ip  ,通过zk配置
    private static String redisServerIP = "192.168.1.127";
    //服务端端口，通过zk配置
    private static int redisServerPort = 6379;

    static Map<String,Object> temp = new HashMap<>();//临时用，连上redis后请将这个删除

    //    private  static  Jedis jedis = null;
    private static Jedis jedis;

    private static RedisUtil redisUtil = null;

    private static JedisPool jedisPool = null;

    //初始化redis连接池
    static{
        JedisPoolConfig config = new JedisPoolConfig();
        //配置最大jedis实例数
        config.setMaxTotal(1000);
        //配置资源池最大闲置数
        config.setMaxIdle(200);
        //等待可用连接的最大时间
        config.setMaxWaitMillis(10000);
        //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的
        config.setTestOnBorrow(true);
        jedisPool = new JedisPool(redisServerIP,redisServerPort);
    }

    //获取Jedis实例
    public synchronized static Jedis getJedis(){
        if(jedisPool != null){
            Jedis resource = jedisPool.getResource();
            return resource;
        }else{
            return null;
        }
    }

    //释放Jedis资源
    public static void returnResource(final Jedis jedis){
        if(jedis != null){
            jedisPool.close();
        }
    }


    public static void main(String[] args){
        jedis = RedisUtil.getJedis();
//        jedis.set("test", "hello JedisPool.");
//        jedis.set("hello", "JedisPool.");
        jedis.sadd("setkey","setvalue1");
        jedis.sadd("setkey","setvalue2");
//        RedisUtil.delBigSet("setkey");
        System.out.println(jedis.scard("setkey"));
        //注意这里不是关闭连接，在JedisPool模式下，Jedis会被归还给资源池
        RedisUtil.returnResource(jedis);
        //测试发现释放Jedis资源后，下面的这个还能返回JedisPool  ????
        System.out.println(jedis.get("hello"));
    }

    private RedisUtil(){
        jedis = new Jedis(redisServerIP , redisServerPort);

    }

    public static synchronized RedisUtil getInstance(){
        if(redisUtil == null) redisUtil = new RedisUtil();
        return redisUtil;
    }

    /**
     * 集合 sets ，添加一个或者多个元素到集合(set)里
     * @param key
     * @param value
     */
    public static void sadd(String key, String value){
        jedis.sadd(key , value);
//        temp.put(key , value);
    }

    /**
     * 集合 sets ，获取集合里面的元素数量
     * @param key
     */
    public static long scard(String key){
        return jedis.scard(key);
//        return temp.size();
    }
    /**
     * 列表 lists 从队列的左边入队一个或多个元素
     * @param key
     * @param value  数组
     */
    public static void lpush(String key , String ... value){
        //jedis.lpush(key , value);
        temp.put(key , value);
    }

//    public static void main(String[] str){
//        RedisUtil ru = RedisUtil.getInstance();
//        ru.sadd("jason1","测试5");
//        ru.lpush("jason2","测试2","测试3");
//        System.out.println("完成");
//    }

    //    Hash删除: hscan + hdel
    public static void delBigHash(String bigHashKey) {
//        Jedis jedis = new Jedis(host, port);
//        if (password != null && !"".equals(password)) {
//            jedis.auth(password);
//        }
        ScanParams scanParams = new ScanParams().count(100);
        String cursor = "0";
        do {
            ScanResult<Map.Entry<String, String>> scanResult = jedis.hscan(bigHashKey, cursor, scanParams);
            List<Map.Entry<String, String>> entryList = scanResult.getResult();
            if (entryList != null && !entryList.isEmpty()) {
                for (Map.Entry<String, String> entry : entryList) {
                    jedis.hdel(bigHashKey, entry.getKey());
                }
            }
            cursor = scanResult.getStringCursor();
        } while (!"0".equals(cursor));
        //删除bigkey
        jedis.del(bigHashKey);
    }

    //    List删除: ltrim
    public static void delBigList(String bigListKey) {
//        Jedis jedis = new Jedis(host, port);
//        if (password != null && !"".equals(password)) {
//            jedis.auth(password);
//        }
        long llen = jedis.llen(bigListKey);
        int counter = 0;
        int left = 100;
        while (counter < llen) {
            //每次从左侧截掉100个
            jedis.ltrim(bigListKey, left, llen);
            counter += left;
        }
        //最终删除key
        jedis.del(bigListKey);
    }

    //    Set删除: sscan + srem
    public static void delBigSet(String bigSetKey) {
//        Jedis jedis = new Jedis(host, port);
//        if (password != null && !"".equals(password)) {
//            jedis.auth(password);
//        }
        ScanParams scanParams = new ScanParams().count(100);
        String cursor = "0";
        do {
            ScanResult<String> scanResult = jedis.sscan(bigSetKey, cursor, scanParams);
            List<String> memberList = scanResult.getResult();
            if (memberList != null && !memberList.isEmpty()) {
                for (String member : memberList) {
                    jedis.srem(bigSetKey, member);
                }
            }
            cursor = scanResult.getStringCursor();
        } while (!"0".equals(cursor));
        //删除bigkey
        jedis.del(bigSetKey);
    }

    //    SortedSet删除: zscan + zrem
    public static void delBigZset(String bigZsetKey){

        ScanParams scanParams = new ScanParams().count(100);
        String cursor = "0";
        do {
            ScanResult<Tuple> scanResult = jedis.zscan(bigZsetKey, cursor, scanParams);
            List<Tuple> tupleList = scanResult.getResult();
            if (tupleList != null && !tupleList.isEmpty()) {
                for (Tuple tuple : tupleList) {
                    jedis.zrem(bigZsetKey, tuple.getElement());
                }
            }
            cursor = scanResult.getStringCursor();
        }while (!"0".equals(cursor));
        //删除bigkey
        jedis.del(bigZsetKey);
    }

}
