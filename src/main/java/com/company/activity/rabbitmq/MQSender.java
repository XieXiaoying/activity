package com.company.activity.rabbitmq;

import com.company.activity.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

@Service
public class MQSender {

    private static Logger log = LoggerFactory.getLogger(MQSender.class);

    @Autowired
    AmqpTemplate amqpTemplate ;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendActivityMessage(ActivityMessage mm) {
        String msg = RedisService.beanToString(mm);
        log.info("send message:"+msg);
        amqpTemplate.convertAndSend(MQConfig.ACTIVITY_QUEUE, msg);
    }

    /**
     * 站内信
     * @param mm
     */
//    public void sendMessage(ActivityMessage mm) {
////		String msg = RedisService.beanToString(mm);
//        log.info("send message:"+"11111");
//        rabbitTemplate.convertAndSend(MQConfig.EXCHANGE_TOPIC,"activity_*", "111111111");
//    }
//
//    /**
//     * 站内信
//     * @param
//     */
//    public void sendRegisterMessage(ActivityMessage miaoShaMessageVo) {
//        String msg = RedisService.beanToString(miaoShaMessageVo);
//        log.info("send message:{}" , msg);
//        rabbitTemplate.convertAndSend(MQConfig.ACTIVITY_TEST,msg);
//    }
}
