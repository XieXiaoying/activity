package com.company.activity.rabbitmq;
import com.company.activity.domain.Order;
import com.company.activity.domain.User;
import com.company.activity.model.ProductModel;
import com.company.activity.redis.RedisService;
import com.company.activity.service.ActivityService;
import com.company.activity.service.OrderService;
import com.company.activity.service.ProductService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
@Service
public class MQReceiver {

    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);

    @Autowired
    RedisService redisService;

    @Autowired
    ActivityService activityService;

    @Autowired
    OrderService orderService;

    @Autowired
    ProductService productService;


//    @RabbitListener(queues=MQConfig.ACTIVITY_QUEUE)
    public void receive(String message) {
        log.info("receive message:"+message);
        ActivityMessage mm = RedisService.stringToBean(message, ActivityMessage.class);
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

