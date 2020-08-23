package com.company.activity.service;

import com.company.activity.dao.OrderDao;
import com.company.activity.domain.Order;
import com.company.activity.domain.OrderInfo;
import com.company.activity.domain.User;
import com.company.activity.model.ProductModel;
import com.company.activity.redis.OrderKey;
import com.company.activity.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {
    @Autowired
    OrderDao orderDao;

    @Autowired
    RedisService redisService;
    public Order getOrderByUserIdAndProductsId(long userId, long productsId){
        return orderDao.getOrderByUserIdAndProductsId(userId, productsId);
    }

    @Transactional
    public OrderInfo createOrder(User user, ProductModel product){
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setProductsCount(1);
        orderInfo.setProductsId(product.getId());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setProductsPrice(product.getCurrentPrice());
        orderInfo.setProductsName(product.getName());
        orderInfo.setUserId(user.getId());
        orderDao.insertActivityOrder(orderInfo);
        Order order = new Order();
        order.setOrderId(orderInfo.getId());
        order.setProductsId(product.getId());
        order.setUserId(user.getId());
        orderDao.insertOrder(order);
        redisService.set(OrderKey.orderKeyByUserIdAndProductsId, "" + user.getNickname() + "_" + product.getId(), order);
        return orderInfo;
    }

    public OrderInfo getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);
    }
}
