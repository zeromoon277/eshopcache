package com.bixkjwfnh.eshop.cache.zookeeper;

import java.util.List;

/**
 * Created by Administrator on 2019/4/4 0004.
 */
public class Demo {
    public static void main(String[] args) throws Exception {
        BaseZookeeper zookeeper = new BaseZookeeper();
        zookeeper.connectZookeeper("148.70.228.127:2181");
//        zookeeper.createNode("/yyc","我是一");
        List<String> children = zookeeper.getChildren("/");
        String result = zookeeper.getData("/lock");
        System.out.println(result);
    }
}
