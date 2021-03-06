package com.company.activity.service;

import com.company.activity.domain.Order;
import com.company.activity.domain.OrderInfo;
import com.company.activity.domain.User;
import com.company.activity.model.ProductModel;
import com.company.activity.redis.ActivityKey;
import com.company.activity.redis.RedisService;
import com.company.activity.utils.MD5Utils;
import com.company.activity.utils.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
@Service
public class ActivityService {
    @Autowired
    RedisService redisService;
    @Autowired
    ProductService productService;
    @Autowired
    OrderService orderService;

    public BufferedImage createVerifyCode(User user, long goodsId) {
        if(user == null || goodsId <=0) {
            return null;
        }
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(ActivityKey.getVerifyCode, user.getNickname()+","+goodsId, rnd);
        //输出图片
        return image;
    }

    /**
     * 注册时用的验证码
     * @param verifyCode
     * @return
     */
    public boolean checkVerifyCodeRegister(int verifyCode) {
        Integer codeOld = redisService.get(ActivityKey.getRegisterVerifyCode,"regitser", Integer.class);
        if(codeOld == null || codeOld - verifyCode != 0 ) {
            return false;
        }
        redisService.delete(ActivityKey.getVerifyCode, "regitser");
        return true;
    }


    public BufferedImage createRegisterVerifyCode() {
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(ActivityKey.getRegisterVerifyCode,"regitser",rnd);
        //输出图片
        return image;
    }

    private static int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            Integer catch1 = (Integer)engine.eval(exp);
            return catch1.intValue();
        }catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public boolean checkVerifyCode(User user, long goodsId, int verifyCode) {
        if(user == null || goodsId <=0) {
            return false;
        }
        Integer codeOld = redisService.get(ActivityKey.getVerifyCode, user.getNickname()+","+goodsId, Integer.class);
        if(codeOld == null || codeOld - verifyCode != 0 ) {
            return false;
        }
        redisService.delete(ActivityKey.getVerifyCode, user.getNickname()+","+goodsId);
        return true;
    }
    public String createActivityPath(User user, long productsId){
        if(user == null || productsId <=0) {
            return null;
        }
        String str = MD5Utils.md5(UUIDUtil.uuid()+"123456");
        redisService.set(ActivityKey.getActivityPath, ""+user.getNickname() + "_"+ productsId, str);
        return str;
    }
    public boolean checkPath(User user, long productsId, String path){
        if(user == null || path == null) {
            return false;
        }
        return redisService.get(ActivityKey.getActivityPath, ""+user.getNickname() + "_"+ productsId, String.class).equals(path);
    }

    @Transactional
    public OrderInfo doComplete(User user, ProductModel product) {
        //减库存 下订单 写入秒杀订单
        boolean success = productService.reduceStock(product);
        if(success){
            System.out.println("xierudingdan");
            return orderService.createOrder(user, product) ;
        }else {
            //如果库存不存在则内存标记为true
            setProductsOver(product.getId());
            return null;
        }
    }
    private void setProductsOver(long productsId){
        redisService.set(ActivityKey.getProductOver, "" + productsId, true);
    }
    private boolean getProductsOver(long productsId){
        return redisService.get(ActivityKey.getProductOver, "" + productsId, boolean.class);
    }
    private static char[] ops = new char[] {'+', '-', '*'};
    /**
     * + - *
     * */
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = ""+ num1 + op1 + num2 + op2 + num3;
        return exp;
    }
    public long getResult(long userId, long productsId) {
        Order order = orderService.getOrderByUserIdAndProductsId(userId, productsId);
        System.out.println(order);
        if(order != null) {//秒杀成功
            return order.getId();
        }else {
            boolean isOver = getProductsOver(productsId);
            if(isOver) {
                return -1;
            }else {
                return 0;
            }
        }
    }

}
