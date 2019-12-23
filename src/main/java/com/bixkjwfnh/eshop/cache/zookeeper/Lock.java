package com.bixkjwfnh.eshop.cache.zookeeper;

/**
 * Created by Administrator on 2019/4/18 0018.
 */
public interface Lock {
    //获取锁资源
    public void getLock();
    //释放锁资源
    public void unlock();
}
