package com.usi.zk;

import com.google.common.io.Files;
import com.usi.zk.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;


public class ZKRecoverUtil {
    public static final Logger logger = LoggerFactory.getLogger(ZKRecoverUtil.class);

    /**
     * 异步进行配置数据本地容灾处理
     * @param content
     * @param path
     * @param recoverDataCache
     */
    public static void doRecover(final byte[] content, final String path, final ConcurrentMap<String, Object> recoverDataCache){

        Executors.newSingleThreadExecutor(new NamedThreadFactory("DYN-CONFIG-RECOVER")).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //进行数据容灾处理
                    Object oldCacheData = recoverDataCache.putIfAbsent(path, content);
                    if (oldCacheData == null) { //第一次 强制刷盘
                        String parentDir = System.getProperty("java.io.tmpdir");
                        File recoveryFile = new File(parentDir + path);
                        Files.createParentDirs(recoveryFile);
                        Files.write(content, recoveryFile);
                    }
                    if (oldCacheData != null && content != oldCacheData) {//数据有更新也刷盘
                        String parentDir = System.getProperty("java.io.tmpdir");
                        File recoveryFile = new File(parentDir + path);
                        Files.createParentDirs(recoveryFile);
                        Files.write(content, recoveryFile);
                    }

                    logger.warn("path {} recover data", path);
                }catch(Exception e){
                    logger.error("DYN-CONFIG-RECOVER error", e);
                }
            }
        });
    }

    /**
     * 从本地装载备份数据
     * @param path
     * @return
     * @throws IOException
     */
    public static byte[] loadRecoverData(String path) throws IOException {
        //进行本地容灾处理
        String parentDir = System.getProperty("java.io.tmpdir");
        File recoveryFile = new File(parentDir + path);

        if(!recoveryFile.exists()){//创建目录
            Files.createParentDirs(recoveryFile);
        }
        //从本地获取配置数据
        return Files.toByteArray(recoveryFile);
    }

}
