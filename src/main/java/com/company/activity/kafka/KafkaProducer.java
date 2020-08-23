package com.company.activity.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.company.activity.rabbitmq.ActivityMessage;
import javax.annotation.Resource;
import com.company.activity.redis.RedisService;

/**
 * Kafka消息生产类
 */
@Component
public class KafkaProducer {
    private static Logger log = LoggerFactory.getLogger(KafkaProducer.class);
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topic.activity}")
    private String topic;//topic名称

    /**
     * 发送用户消息
     *
     * @param mm 用户信息
     */
    public void sendActivityMessage(ActivityMessage mm) {
        String msg = RedisService.beanToString(mm);
        log.info("send message:"+msg);
        kafkaTemplate.send(topic, msg);
    }
}