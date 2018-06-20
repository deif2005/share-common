package com.usi.zk;

import com.usi.zk.util.AbstractLifecycle;
import com.usi.zk.util.ConfigLoader;
import com.usi.zk.util.NetUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;

/**
 * zk客户端
 * <p/>
 * 创建时间: 14-8-6 下午1:52<br/>
 * @since v0.0.1
 */
public class ZKClient extends AbstractLifecycle {
    public static final Logger logger = LoggerFactory.getLogger(ZKClient.class);

    /** 云管理中心域名 */
    public static final String DEFAULT_DOMAIN_NAME = "wxtest.usisz.com";

    private volatile static CuratorFramework zkClient = null;

    private ZKClient(){};

    @Override
    protected void doStart() {
        isStart = true;
        String ip = null;
        String localIp = null;
        try {
//            ip = NetUtil.getIpByDomain(DEFAULT_DOMAIN_NAME);
            localIp = NetUtil.getLocalHost();
            ip = ConfigLoader.getInstance().getProperty(localIp+".zkip");
        } catch (Exception e) {
            logger.error("getIpByDomain error!", e);
            System.exit(-1);
        }
        String url = ip + ":" + ConfigLoader.getInstance().getProperty(localIp+".zkport");
        zkClient = CuratorFrameworkFactory.newClient(url, new ExponentialBackoffRetry(1000, 3));
        //innerRegisterListeners(zkClient);

        zkClient.start();
        logger.warn("ZKClient start success!");
    }

    /**
     * 根据ip获取zk client, 可以直接调用该方法，但不建议 请使用 ZKClientManager 调用
     * @param ip
     * @return
     */
    public static CuratorFramework create(String ip){
        logger.warn(" start conn zk server {} ", ip);
        String url="";
        CuratorFramework newClient = null;
        synchronized (ip){
            String port = ConfigLoader.getInstance().getProperty("zk.port");
            if (port != null && !"".equals(port)){
                url = ip + ":" + port;
            }else{
                url = ip + ":" + "2181";
            }
            newClient = CuratorFrameworkFactory.newClient(url, new ExponentialBackoffRetry(1000, 3));
            //innerRegisterListeners(zkClient);
            newClient.start();
        }

        logger.warn("  conn zk server {} success!", ip);
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
