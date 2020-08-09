package com.company.activity.controller;

import com.company.activity.common.resultbean.ResponseResult;
import com.company.activity.domain.User;
import com.company.activity.service.ActivityService;
import com.company.activity.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;

import static com.company.activity.common.enums.ResultStatus.*;
@Controller
@RequestMapping("/user")
public class RegisterController {

    private static Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private ActivityService activityService ;

    @RequestMapping("/register")
    public String register(){
        return "register";
    }

    /**
     * 注册网站
     * @param userName
     * @param passWord
     * @param salt
     * @return
     */
    @RequestMapping("/to_register")
    @ResponseBody
    public ResponseResult<String> toRegister(@RequestParam("username") String userName ,
                                           @RequestParam("password") String passWord,
                                           @RequestParam("verifyCode") String verifyCode,
                                           @RequestParam("salt") String salt,
                                           HttpServletResponse response ){

        ResponseResult<String> result = ResponseResult.build();
        /**
         * 校验验证码
         */
        boolean check = activityService.checkVerifyCodeRegister(Integer.valueOf(verifyCode));
        if(!check){
            result.withError(CODE_FAIL.getCode(),CODE_FAIL.getMessage());
            return result;

        }
        boolean registerInfo  = userService.register(response , userName,passWord,salt);
        if(!registerInfo){
            result.withError(RESIGETER_FAIL.getCode(),RESIGETER_FAIL.getMessage());
            return result;
        }
        return result;
    }

    @RequestMapping(value = "/registerVerifyCode", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult<String> getRegisterCode(HttpServletResponse response){
        ResponseResult<String> result = ResponseResult.build();
        try {
            BufferedImage image = activityService.createRegisterVerifyCode();
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return result;
        } catch (Exception e) {
            logger.error("生成验证码错误-----注册:{}", e);
            result.withError(MIAOSHA_FAIL.getCode(), MIAOSHA_FAIL.getMessage());
            return result;
        }
    }

}