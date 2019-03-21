package com.wd.zk.util;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 配置文件装载器
 * 注意本地配置文件一般只存储初始化时需要的信息，不应该放置太多的配置信息（运行期可变的配置信息更不能放置在此），
 * 所以此类只支持一个配置文件
 *
 */
public class ConfigLoader {
    public static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);

    public static final String APP_MAINCONF_FILE = "config/start-conf.properties";
//    public static final String APP_MAINCONF_FILE_DOUBLECHECK = "config/main-conf.properties";
    public static final String COMMON_MAINCONF_FILE = "common_config/start-conf.properties";
//    public static final String COMMON_MAINCONF_FILE_DOUBLECHECK = "common_config/main-conf.properties";
    private static final String DEFAULT_CONFIG_FILE_KEY = "configFile";

    private static class ConfigLoaderHolder{
        private static final ConfigLoader instance = new ConfigLoader();
    }

    private ConfigLoader(){}

    private static Properties properties = null;

    static{
        properties = new Properties();
        try {
            //获取系统参数
            String confFileName = System.getProperty(DEFAULT_CONFIG_FILE_KEY);
            System.out.println(Thread.currentThread().getContextClassLoader().getResource(".").getPath());
            if(Strings.isNullOrEmpty(confFileName)) {//没有配置 从classloader获取
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(APP_MAINCONF_FILE);
                if(is == null){
                    is = Thread.currentThread().getContextClassLoader().getResourceAsStream(COMMON_MAINCONF_FILE);
                }
                if (null != is)
                    properties.load(is);
            } else {
                properties.load(new FileInputStream(new File(confFileName)));
            }
        } catch (IOException e) {
            logger.error("load properties {} error", DEFAULT_CONFIG_FILE_KEY, e);
            System.exit(-1);
        }
    }

    /**
     * 单例获取ConfigLoader
     * @return
     */
    public static ConfigLoader getInstance(){
        return ConfigLoaderHolder.instance;
    }

    public String getProperty(String key){
        return properties.getProperty(key);
    }
}
