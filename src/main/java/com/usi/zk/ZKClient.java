package com.wd.zk;

import com.google.common.base.Strings;
import com.wd.zk.util.AbstractLifecycle;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * zk客户端
 *
 */
public class ZKClient extends AbstractLifecycle {
    public static final Logger logger = LoggerFactory.getLogger(ZKClient.class);

    /** 云管理中心域名 */
//    public static final String DEFAULT_DOMAIN_NAME = "wxtest.usisz.com";

    private volatile static CuratorFramework zkClient = null;

    private ZKClient(){};

    @Override
    protected void doStart() {
        isStart = true;
        String zkIp;
        String url="";
        String zkPort;
        try {
//            ip = NetUtil.getIpByDomain(DEFAULT_DOMAIN_NAME);
            zkIp = ConfigLoader.getInstance().getProperty("localhost.zkip");
            zkPort = ConfigLoader.getInstance().getProperty("localhost.zkport");
            if (!Strings.isNullOrEmpty(zkIp) && !Strings.isNullOrEmpty(zkPort)){
                url = zkIp + ":" + zkPort;
            }else {
                List<String> localIps = NetUtil.getLocalIps();
                for (String localIp:localIps){
                    zkIp = ConfigLoader.getInstance().getProperty(localIp+".zkip");
                    if(!Strings.isNullOrEmpty(zkIp)) {
                        zkPort = ConfigLoader.getInstance().getProperty(localIp+".zkport");
                        if (zkPort != null){
                            url = zkIp + ":" + zkPort;
                        }else{
                            logger.error("zookeeper config not specified!");
                            throw new RuntimeException("zookeeper config not specified!");
                        }
                        break;
                    }
                }
            }
            zkClient = CuratorFrameworkFactory.newClient(url, new ExponentialBackoffRetry(1000,
                    3));
            zkClient.start();
            logger.warn("ZKClient start success!");
        } catch (Exception e) {
            logger.error("startZookeeper error!", e);
            System.exit(-1);
        }
    }

    /**
     * 根据ip获取zk client, 可以直接调用该方法，但不建议 请使用 ZKClientManager 调用
     * @param ip
     * @return
     */
    public static CuratorFramework create(String ip){
        logger.warn(" start conn zk server {} ", ip);
        String url="";
        CuratorFramework newClient;
        synchronized (ip){
            String port = ConfigLoader.getInstance().getProperty("zk.port");
            if (port != null && !"".equals(port)){
                url = ip + ":" + port;
            }else{
                url = ip + ":" + "2181";
            }
            newClient = CuratorFrameworkFactory.newClient(url, new ExponentialBackoffRetry(1000,
                    3));
            //innerRegisterListeners(zkClient);
            newClient.start();
        }
        logger.warn("conn zk server {} success!", ip);
        return newClient;
    }

    @Override
    public void stop() {
        if(zkClient != null) {
            zkClient.close();
        }
        //throw new RuntimeException("un implemented");
    }

    private static class ZKClientHolder{
        private static final ZKClient instance = new ZKClient();
    }

    /**
     * 获取zk客户端实例（单例）
     * @return
     */
    public static CuratorFramework getClient(){
        //初始化client
        ZKClientHolder.instance.start();
        return zkClient;
    }
}
