package com.bixkjwfnh.eshop.cache.kafka;

/**
 * Created by Administrator on 2019/4/8 0008.
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bixkjwfnh.eshop.cache.model.ProductInventory;
import com.bixkjwfnh.eshop.cache.service.ProductInventoryService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 消费者
 * 使用@KafkaListener注解,可以指定:主题,分区,消费组
 */
@Component
public class KafkaConsumer {

    @Autowired
    private ProductInventoryService productInventoryService;

    @KafkaListener(topics = {"test"})
    public void listen(ConsumerRecord<?, ?> record){

        Optional<?> kafkaMessage = Optional.ofNullable(record.value());

        if (kafkaMessage.isPresent()) {

            Object message = kafkaMessage.get();
            System.out.println("---->"+record);
            System.out.println("---->"+message);
            JSONObject json = JSON.parseObject(message.toString());
            if(json != null && json.getString("type").equals("save")){
                Integer productID = json.getInteger("productId");
                Long inventoryCnt = json.getLong("inventoryCnt");
                ProductInventory productInventory = new ProductInventory();
                productInventory.setProductId(productID);
                productInventory.setInventoryCnt(inventoryCnt);
                productInventoryService.setProductInventoryCache(productInventory);

                productInventoryService.saveProductInventory(productInventory);
            }

            if(json != null && json.getString("type").equals("find")){
                Integer productID = json.getInteger("productId");

                ProductInventory product = productInventoryService.findProductInventory(productID);
                System.out.println(123);
            }

        }

    }
}
