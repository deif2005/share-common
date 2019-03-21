package com.wd.zk.util;


/**
 * 生命周期抽象类
 *
 */
public abstract class AbstractLifecycle implements ILifecycle {
    protected volatile boolean isStart = false;

    @Override
    public void start() {
        if(!isStart){
            doStart();
            isStart = true;
        }
    }

    @Override
    public boolean isStarted() {
        return isStart;
    }

    /**
     * 进行实际的启动操作
     */
    protected abstract void doStart();
}
