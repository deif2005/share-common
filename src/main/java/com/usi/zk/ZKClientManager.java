package com.usi.zk;


import org.apache.curator.framework.CuratorFramework;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * zk client管理类
 *
 */
public class ZKClientManager {
    private static ConcurrentMap<String, CuratorFramework> zkClientMap = new ConcurrentHashMap<String, CuratorFramework>();

    private ZKClientManager(){};

    public static CuratorFramework getClient(String ip){
        if(ip == null || ip.trim().length() == 0){
            throw new IllegalArgumentException("zk ip is null!");
        }

        synchronized (ip) {
            if (!zkClientMap.containsKey(ip)) {
                CuratorFramework client = ZKClient.create(ip);

                CuratorFramework oldClient = zkClientMap.putIfAbsent(ip, client);
                if (oldClient != null) {
                    //close old client
                    oldClient.close();
                }

                return client;
            }
        }
        return zkClientMap.get(ip);
    }

    public static void main(String[] args) {
        CuratorFramework curatorFramework = getClient("112.74.104.64");
        try {
            String jdbcStr = "1";
            String jdbcStr2 = "2";
            curatorFramework.setData().forPath("/start-configs/zkProperties",jdbcStr.getBytes());
//            curatorFramework.setData().forPath("/start-configs/zkProperties/password",jdbcStr2.getBytes());
//            if (curatorFramework.checkExists().forPath("/start-configs/zkProperties/config") != null) {
//                byte[] data = curatorFramework.getData().forPath("/start-configs/zkProperties/jdbc_url");
//                String jdbc_url = new String(data);
////                if (Strings.isNullOrEmpty(jdbc_url)){
//                    byte[] data1 = jdbcStr.getBytes();
//                    curatorFramework.setData().forPath("/start-configs/zkProperties/jdbc_url",data1);
////                }
//                System.out.println("/jdbc_url:"+jdbc_url);
//            }else {
//                byte[] data1 = jdbcStr.getBytes();
//                curatorFramework.create().creatingParentsIfNeeded().forPath("/start-configs/zkProperties/jdbc_url",data1);
//                byte[] data2 = curatorFramework.getData().forPath("/start-configs/zkProperties/jdbc_url");
//                System.out.println(new String(data2));
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
