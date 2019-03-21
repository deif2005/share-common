package com.wd.zk.util;

/**
 * 生命周期管理接口
 * <p/>
 * 创建时间: 14-8-8 下午4:37<br/>
 */
public interface ILifecycle {
    void start();
    void stop();
    boolean isStarted();
}
