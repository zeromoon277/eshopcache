package com.bixkjwfnh.eshop.cache.zookeeper;

import org.I0Itec.zkclient.ZkClient;

/**
 * Created by Administrator on 2019/4/18 0018.
 */
public abstract class ZookeeperAbstractLock  implements Lock{
    //zk连接地址
    private static final String CONNECTSTRING = "148.70.228.127:2181";
    //创建zk连接
    protected ZkClient zkClient = new ZkClient(CONNECTSTRING);
    //PATH
    protected static final String PATH = "/llock";

    @Override
    public void getLock() {
        if(trylock()){
            System.out.println("###获取lock锁当资源###");
        } else{
            //等待锁
            waitLock();
            //重新获取锁
            getLock();
        }
    }

    @Override
    public void unlock() {
        if(zkClient!=null){
            zkClient.delete(PATH);
            zkClient.close();
            System.out.println("###释放所资源###");
        }
    }

    /**
     * 判断是否获取锁成功，成功返回true，失败返回false
     */
    abstract boolean trylock();
    /**
     * 等待锁
     */
    abstract void waitLock();
}
