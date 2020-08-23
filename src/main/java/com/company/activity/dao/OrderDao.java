package com.company.activity.dao;

import com.company.activity.domain.Order;
import com.company.activity.domain.OrderInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;

@Mapper
public interface OrderDao {
    @Select("select * from activity_order where user_id = #{userId} and products_id = #{productsId}")
    Order getOrderByUserIdAndProductsId(long userId, long productsId);

    @Insert("insert into order_info1(user_id, products_id, products_name, products_count, products_price, order_channel, status, create_date)values("
            + "#{userId}, #{productsId}, #{productsName}, #{productsCount}, #{productsPrice}, #{orderChannel},#{status},#{createDate} )")
    @SelectKey(keyColumn="id", keyProperty="id", resultType=long.class, before=false, statement="select last_insert_id()")
    long insertActivityOrder(OrderInfo orderInfo);

    @Insert("insert into activity_order(user_id, products_id, order_id) values (#{userId}, #{productsId}, #{orderId})")
    int insertOrder(Order order);

    @Select("select * from order_info1 where id = #{orderId}")
    OrderInfo getOrderById(long orderId);
}
