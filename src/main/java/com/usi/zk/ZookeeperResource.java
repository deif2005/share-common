package com.usi.zk;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import com.usi.encrypt.AESUtil;
import com.usi.encrypt.EncryptUtil;
import com.usi.zk.util.ConfigLoader;
import com.usi.zk.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.AbstractResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * 来源于zookeeper的配置文件资源，该配置文件只做第一次装载，不做动态处理？
 *
 */
public class ZookeeperResource extends AbstractResource implements ApplicationContextAware, DisposableBean {
    private static Logger log = LoggerFactory.getLogger(ZookeeperResource.class);

    public static final String URL_HEADER = "zk://";
    public static String START_PATH = "/%s/start-configs";
    public static String DEVICE_PLATFORM_PATH = "/%s/deviceplatform";

    /**
     * 多产品线支持 2015-08-19 add
     */
//    private static final String CLOUD_PATH_FORMAT = "/start-configs/%s/%s/config";
    private static String path = "";//String.format(PATH_FORMAT,CloudContextFactory.getCloudContext().getApplicationName() );
//    private String cloud_path = String.format(CLOUD_PATH_FORMAT, CloudContextFactory.getCloudContext().getProductCode(), CloudContextFactory.getCloudContext().getApplicationName());
    ConcurrentMap<String, Object> recoverDataCache = Maps.newConcurrentMap();

    AbstractApplicationContext ctx;

    static {
        String projectName = ConfigLoader.getInstance().getProperty("project.name");
        if (Strings.isNullOrEmpty(projectName)){
            String projectPath = System.getProperty("user.dir");
            projectName = projectPath.substring(projectPath.lastIndexOf("\\")+1,projectPath.length());
        }
        try {
            List<String> localIps = NetUtil.getLocalIps();
            for (String ip:localIps){
                String rootNode = ConfigLoader.getInstance().getProperty(ip+".root");
                if (!Strings.isNullOrEmpty(rootNode)){
                    String rootStr = rootNode.substring(1,rootNode.indexOf("/",1));
                    path = String.format(rootNode,projectName);
                    START_PATH = String.format(START_PATH,rootStr);
                    DEVICE_PLATFORM_PATH = String.format(DEVICE_PLATFORM_PATH,rootStr);
                    break;
                }
            }
            if ("".equals(path)){
                throw new RuntimeException("zookeeper config not specified");
            }
        }catch (SocketException e){
            log.error("getlocalip error ",e);
        }
    }

    @Override
    public boolean exists() {
        try {
            return null != ZKClient.getClient().checkExists().forPath("");
        } catch (Exception e) {
            log.error("Falied to detect the config in zoo keeper.", e);
            return false;
        }
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public URL getURL() throws IOException {
        return new URL(URL_HEADER + path);
    }

    @Override
    public String getFilename() throws IllegalStateException {
        return path;
    }

    @Override
    public String getDescription() {
        return "Zookeeper resouce at :'" + path;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        byte[] data = null;
        byte[] tmp1 = new byte[0];
        byte[] tmpByte1;
        String rStr = System.getProperty("line.separator");
        try {
            if (ZKClient.getClient().checkExists().forPath(path) != null) { // cloud mode, NODE: /startconfigs/%s/%s/config
                // miou 2018.06.22
                List<String> pathList = ZKClient.getClient().getChildren().forPath(path);
                for (String cPath:pathList){
                    String childNodeName = cPath.substring(cPath.lastIndexOf("\\")+1,cPath.length());
                    String childNodeValue = new String(ZKClient.getClient().getData().forPath(path+"/"+cPath));
                    String nameAndValue = childNodeName+"="+childNodeValue+rStr;
                    tmpByte1 = nameAndValue.getBytes();
                    data = new byte[tmpByte1.length+tmp1.length];
                    //tmpByte+tmp1-->data
                    System.arraycopy(tmpByte1,0,data,0,tmpByte1.length);
                    if (tmp1.length > 0){
                        System.arraycopy(tmp1,0,data,tmpByte1.length,tmp1.length);
                    }
                    //data-->tmp1
                    tmp1 = new byte[data.length];
                    System.arraycopy(data,0,tmp1,0,data.length);
                }
            } else { //cloud mode
                log.error("{} and {} none exists", "", path);
                System.exit(-1);
            }
        } catch (Exception e) {
            log.error("zk server error", e);
            // 读取cmc配置失败时加载本地备份的配置
            try {
                data = ZKRecoverUtil.loadRecoverData(path);
            } catch (Exception e1) {
                log.error("zk server cloud_path error", e);
                data = ZKRecoverUtil.loadRecoverData(path);
            }

        }

        // 备份cmc配置到本地
        ZKRecoverUtil.doRecover(data, path, recoverDataCache);

        log.debug("init get startconfig data {}", new String(data));
        if (EncryptUtil.isEncrypt(data)) {
            byte[] pureData = new byte[data.length - 2];
            System.arraycopy(data, 2, pureData, 0, data.length - 2);
            String originStr = null;
            try {
                originStr = AESUtil.aesDecrypt(new String(pureData), EncryptUtil.encryptKey);
            } catch (Exception e) {
                log.error("decrypt error", e);
                System.exit(-1);
            }
            return new ByteArrayInputStream(originStr.getBytes());
        } else {
            return new ByteArrayInputStream(data);
        }
    }


    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.ctx = (AbstractApplicationContext) ctx;
    }

    @Override
    public void destroy() throws Exception {
        log.info("Destory Zookeeper Resouce.");
        //TODO destory zk connection
//        if (executor != null) {
//            log.info("Close connection to Zookeeper Server.");
//            try {
//                //executor.getZk().close();
//                log.info("Connection to Zookeeper Server closed.");
//            } catch (Exception e) {
//                log.error("Error found when close zookeeper connection.", e);
//            }
//        }
    }
}