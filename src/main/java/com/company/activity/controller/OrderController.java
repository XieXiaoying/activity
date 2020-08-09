package com.company.activity.controller;

import com.company.activity.common.resultbean.ResponseResult;
import com.company.activity.domain.OrderInfo;
import com.company.activity.domain.User;
import com.company.activity.model.OrderDetail;
import com.company.activity.model.ProductModel;
import com.company.activity.redis.RedisService;
import com.company.activity.service.OrderService;
import com.company.activity.service.ProductService;
import com.company.activity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;
import static com.company.activity.common.enums.ResultStatus.SESSION_ERROR;
import static com.company.activity.common.enums.ResultStatus.ORDER_NOT_EXIST;
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;

    @Autowired
    ProductService productsService;

    @RequestMapping("/detail")
    @ResponseBody
    public ResponseResult<OrderDetail> info(Model model,
                                            User user,
                                            @RequestParam("orderId") long orderId) {
        ResponseResult<OrderDetail> result = ResponseResult.build();
        if (user == null) {
            result.withError(SESSION_ERROR.getCode(), SESSION_ERROR.getMessage());
            return result;
        }
        OrderInfo order = orderService.getOrderById(orderId);
        if(order == null) {
            result.withError(ORDER_NOT_EXIST.getCode(), ORDER_NOT_EXIST.getMessage());
            return result;
        }
        long productsId = order.getProductsId();
        ProductModel products = productsService.getProductById(productsId);
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderInfo(order);
        orderDetail.setProductModel(products);
        result.setData(orderDetail);
        return result;
    }
}
