package com.usi;

import com.usi.zk.ZookeeperConfigurer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author miou
 * @date 2018/6/21
 */
public class Maintest {
    private static final Log log = LogFactory.getLog(Maintest.class);

    public static void main(String[] args) {
        try {
            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("SpringContext.xml");
            context.start();
            ZookeeperConfigurer zookeeperConfigurer = (ZookeeperConfigurer)context.getBean("zkPropConfigurer");
            String jdbc_url = (String) zookeeperConfigurer.getAppliedPropertySources().get("localProperties").getProperty("jdbc_url");
            String jdbc_user = (String) zookeeperConfigurer.getAppliedPropertySources().get("localProperties").getProperty("jdbc_user");
            String jdbc_password = (String) zookeeperConfigurer.getAppliedPropertySources().get("localProperties").getProperty("jdbc_password");
            System.out.println(jdbc_url+" "+jdbc_user+" "+jdbc_password);
        } catch (Exception e) {
            log.error("error: "+e);
            return;
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
