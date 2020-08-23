package com.company.activity.kafka;

import com.company.activity.domain.Order;
import com.company.activity.domain.User;
import com.company.activity.model.ProductModel;
import com.company.activity.rabbitmq.ActivityMessage;
import com.company.activity.redis.RedisService;
import com.company.activity.service.ActivityService;
import com.company.activity.service.OrderService;
import com.company.activity.service.ProductService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Properties;

@Component
public class KafkaConsumer {
    private final org.slf4j.Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    @Autowired
    RedisService redisService;

    @Autowired
    ActivityService activityService;

    @Autowired
    OrderService orderService;

    @Autowired
    ProductService productService;

    @KafkaListener(topics = {"${kafka.topic.activity}"})
    public void listen(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {

            Object message = kafkaMessage.get();


            log.info("receive message:"+ message);
            ActivityMessage mm = RedisService.stringToBean(message.toString(), ActivityMessage.class);
            User user = mm.getUser();
            long productsId = mm.getProductsId();

            ProductModel product = productService.getProductById(productsId);
            int stock = product.getStockCount();
            if(stock <= 0) {
                return;
            }
            //判断是否已经秒杀到了
            Order order = orderService.getOrderByUserIdAndProductsId(Long.valueOf(user.getNickname()), productsId);
            if(order != null) {
                return;
            }
            //减库存 下订单 写入秒杀订单
            try{
                activityService.doComplete(user, product);
            }catch (Exception e){
                log.error(e.getCause().getMessage());
            }
        }



    }
}