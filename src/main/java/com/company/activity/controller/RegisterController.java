package com.company.activity.controller;

import com.company.activity.common.resultbean.ResponseResult;
import com.company.activity.service.ActivityService;
import com.company.activity.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import static com.company.activity.common.enums.ResultStatus.*;
@Controller
@RequestMapping("/user")
public class RegisterController {

    private static Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private ActivityService activityService ;

    @RequestMapping("/do_register")
    public String registerIndex(){
        return "register";
    }

    /**
     * 注册网站
     * @param userName
     * @param passWord
     * @param salt
     * @return
     */
    @RequestMapping("/register")
    @ResponseBody
    public ResponseResult<String> register(@RequestParam("username") String userName ,
                                           @RequestParam("password") String passWord,
                                           @RequestParam("verifyCode") String verifyCode,
                                           @RequestParam("salt") String salt, HttpServletResponse response ){

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
}