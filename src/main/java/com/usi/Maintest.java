package com.wd;

import com.wd.zk.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author miou
 * @date 2018/6/21
 */
public class Maintest {
    private static final Log log = LogFactory.getLog(Maintest.class);

    public static void main(String[] args) {
//        try {
//            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("resources/SpringContext.xml");
//            context.start();
//            ZookeeperConfigurer zookeeperConfigurer = (ZookeeperConfigurer)context.getBean("zkPropConfigurer");
//            String jdbc_url = (String) zookeeperConfigurer.getAppliedPropertySources().get("localProperties").getProperty("jdbc_url");
//            String jdbc_user = (String) zookeeperConfigurer.getAppliedPropertySources().get("localProperties").getProperty("jdbc_user");
//            String jdbc_password = (String) zookeeperConfigurer.getAppliedPropertySources().get("localProperties").getProperty("jdbc_password");
//            System.out.println(jdbc_url+" "+jdbc_user+" "+jdbc_password);
//        } catch (Exception e) {
//            log.error("error: "+e);
//            return;
//        }
        try {
//            DynConfigClient dynConfigClient = DynConfigClientFactory.getClient("47.105.238.105");
//            ChangeListener changeListener = new ChangeListener();
//            dynConfigClient.registerListeners("/dev-environment/static-config",changeListener);
//            CuratorFramework client = ZKClientManager.getClient("47.105.238.105");
//            DynConfigClient dynConfigClient = DynConfigClientFactory.getClient();
//            ChangeListener changeListener = new ChangeListener();
//            dynConfigClient.registerListeners1("/dev-environment/static-config",changeListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        synchronized (Maintest.class) {
            while (true) {
                try {
                    Maintest.class.wait();
                } catch (InterruptedException e) {
                    log.error("== synchronized error:",e);
                }
            }
        }
    }
}
