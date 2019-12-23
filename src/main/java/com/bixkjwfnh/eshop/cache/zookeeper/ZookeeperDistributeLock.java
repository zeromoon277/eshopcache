package com.bixkjwfnh.eshop.cache.zookeeper;

import org.I0Itec.zkclient.IZkDataListener;

import java.util.concurrent.CountDownLatch;

/**
 * Created by Administrator on 2019/4/18 0018.
 */
public class ZookeeperDistributeLock extends ZookeeperAbstractLock{

    private CountDownLatch countDownLatch = null;

    @Override
    boolean trylock() {
        try {
            zkClient.createPersistent(PATH);
            return true;
        } catch (RuntimeException e) {
            //e.printStackTrace();
            return false;
        }
    }

    @Override
    void waitLock() {
        IZkDataListener iZkDataListener = new IZkDataListener() {

            @Override
            public void handleDataChange(String s, Object o) throws Exception {

            }

            @Override
            public void handleDataDeleted(String s) throws Exception {
                //唤醒被等待的线程
                if (countDownLatch!=null) {
                    countDownLatch.countDown();
                }
            }
        };

        //注册事件
        zkClient.subscribeDataChanges(PATH,iZkDataListener);
        if(zkClient.exists(PATH)){
            countDownLatch = new CountDownLatch(1);
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //删除监听
        zkClient.unsubscribeDataChanges(PATH,iZkDataListener);
    }
}
