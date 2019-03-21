package com.wd.zk;


import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;

import java.util.concurrent.Executor;

/**
 * 数据改变监听器
 */
public interface IChangeListener {
    /**
     * 返回线程池执行器
     * @return
     */
    Executor getExecutor();

    /**
     * 接收到配置项文件处理
     * @param configuration
     */
    void receiveConfigInfo(final Configuration configuration);

}
