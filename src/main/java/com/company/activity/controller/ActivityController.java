package com.company.activity.controller;

import com.company.activity.access.AccessLimit;
import com.company.activity.common.resultbean.ResponseResult;
import com.company.activity.domain.Order;
import com.company.activity.domain.User;
import com.company.activity.kafka.KafkaProducer;
import com.company.activity.model.ProductDetail;
import com.company.activity.model.ProductModel;
import com.company.activity.rabbitmq.ActivityMessage;
import com.company.activity.rabbitmq.MQSender;
import com.company.activity.redis.ActivityKey;
import com.company.activity.redis.ProductKey;
import com.company.activity.redis.RedisService;
import com.company.activity.service.ActivityService;
import com.company.activity.service.OrderService;
import com.company.activity.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import static com.company.activity.common.enums.ResultStatus.*;
@Controller
@RequestMapping("/activity")
public class ActivityController implements InitializingBean {
    private static Logger logger = LoggerFactory.getLogger(ActivityController.class);
    @Autowired
    ActivityService activityService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @Autowired
    KafkaProducer kafkaProducer;
//    MQSender mqSender;

    @Autowired
    ProductService productService;

    private HashMap<Long, Boolean> localOverMap = new HashMap<Long, Boolean>();

    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult<String> getVerifyCode(HttpServletResponse response,
                                                User user,
                                                @RequestParam(value = "productsId") long productsId){
        ResponseResult<String> result = ResponseResult.build();
        if (user == null) {
            result.withError(SESSION_ERROR.getCode(), SESSION_ERROR.getMessage());
            return result;
        }
        try {
            BufferedImage image = activityService.createVerifyCode(user, productsId);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return result;
        } catch (Exception e) {
            logger.error("生成验证码错误-----productsId:{}", productsId, e);
            result.withError(MIAOSHA_FAIL.getCode(), MIAOSHA_FAIL.getMessage());
            return result;
        }
    }

    @AccessLimit(seconds = 5, maxCount = 200, needLogin = true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult<String> getActivityPath(HttpServletRequest request,
                                                 User user,
                                                 @RequestParam("productsId") long productsId,
                                                 @RequestParam(value = "verifyCode", defaultValue = "0") int verifyCode
    ) {
        ResponseResult<String> result = ResponseResult.build();
        if (user == null) {
            System.out.println("user === null");
            result.withError(SESSION_ERROR.getCode(), SESSION_ERROR.getMessage());
            return result;
        }
        boolean check = activityService.checkVerifyCode(user, productsId, verifyCode);
        if (!check) {
            result.withError(REQUEST_ILLEGAL.getCode(), REQUEST_ILLEGAL.getMessage());
            return result;
        }
        String path = activityService.createActivityPath(user, productsId);
        result.setData(path);
        return result;
    }

    @AccessLimit(seconds = 5, maxCount = 200, needLogin = true)
    @RequestMapping(value="/{path}/do_buy", method= RequestMethod.POST)
    @ResponseBody
    public ResponseResult<Integer> ToBuy(Model model,
                                         User user,
                                         @PathVariable("path") String path,
                                         @RequestParam("productsId") long productsId) {
        System.out.println(productsId);
        ResponseResult<Integer> result = ResponseResult.build();
        if (user == null) {
            result.withError(SESSION_ERROR.getCode(), SESSION_ERROR.getMessage());
            return result;
        }
        //验证path
        boolean check = activityService.checkPath(user, productsId, path);
        if (!check) {
            result.withError(REQUEST_ILLEGAL.getCode(), REQUEST_ILLEGAL.getMessage());
            return result;
        }
//		//使用RateLimiter 限流
//		RateLimiter rateLimiter = RateLimiter.create(10);
//		//判断能否在1秒内得到令牌，如果不能则立即返回false，不会阻塞程序
//		if (!rateLimiter.tryAcquire(1000, TimeUnit.MILLISECONDS)) {
//			System.out.println("短期无法获取令牌，真不幸，排队也瞎排");
//			return ResultGeekQ.error(CodeMsg.MIAOSHA_FAIL);
//
//		}

        //是否已经秒杀到
        Order order = orderService.getOrderByUserIdAndProductsId(Long.valueOf(user.getNickname()), productsId);
        if (order != null) {
            result.withError(REPEATE_MIAOSHA.getCode(), REPEATE_MIAOSHA.getMessage());
            return result;
        }
        //内存标记，减少redis访问
        boolean over = localOverMap.getOrDefault(productsId, false);//.get(productsId) == null ? false : localOverMap.get(productsId);
        if (over) {
            result.withError(MIAO_SHA_OVER.getCode(), MIAO_SHA_OVER.getMessage());
            return result;
        }
        //预见库存
        System.out.println(redisService.get(ProductKey.productStock, "" + productsId, Long.class));
        Long stock = redisService.decr(ProductKey.productStock, "" + productsId);
        System.out.println(redisService.get(ProductKey.productStock, "" + productsId, Long.class));
        System.out.println(stock);
        if (stock < 0) {
            localOverMap.put(productsId, true);
            result.withError(MIAO_SHA_OVER.getCode(), MIAO_SHA_OVER.getMessage());
            return result;
        }
        ActivityMessage message = new ActivityMessage();
        message.setProductsId(productsId);
        message.setUser(user);
        kafkaProducer.sendActivityMessage(message);
        return result;
    }
    /**
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     */
    @AccessLimit(seconds = 5, maxCount = 200, needLogin = true)
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult<Long> result(Model model,
                                       User user,
                                       @RequestParam("productsId") long productsId) {
        ResponseResult<Long> result = ResponseResult.build();
        if (user == null) {
            result.withError(SESSION_ERROR.getCode(), SESSION_ERROR.getMessage());
            return result;
        }
        model.addAttribute("user", user);
        System.out.println(Long.valueOf(user.getNickname()));
        long ProductResult = activityService.getResult(user.getId(), productsId);
        result.setData(ProductResult);
        return result;
    }
    /**
     * 系统初始化
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<ProductModel> productsList = productService.getProductList();
        if (productsList == null) {
            return;
        }
        System.out.println("init: ");
        for (ProductModel product : productsList) {
            System.out.println(product.getId() + " " + product.getStockCount());
            redisService.set(ProductKey.productStock, "" + product.getId(), product.getStockCount());
            localOverMap.put(product.getId(), false);
            redisService.set(ActivityKey.getProductOver, "" + product.getId(), false);
        }
        System.out.println("test");
    }
}
