package com.wd.zk;


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
}
