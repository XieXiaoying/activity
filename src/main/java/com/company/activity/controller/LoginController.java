package com.company.activity.controller;

import com.company.activity.common.resultbean.ResponseResult;
import com.company.activity.redis.redismanager.RedisScript;
import com.company.activity.service.UserService;
import com.company.activity.model.LoginModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static com.company.activity.common.Constanst.COUNTLOGIN;

@Controller
@RequestMapping("/login")
public class LoginController {

    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;
//    @Reference
    @RequestMapping("/login")
    public String tologin(Model model) {
        RedisScript.addCountByUserKey(COUNTLOGIN);
        String count = RedisScript.getVisitCountByUserKey(COUNTLOGIN).toString();
        logger.info("访问网站的次数为:{}",count);
        model.addAttribute("count",count);
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public ResponseResult<Boolean> dologin(HttpServletResponse response, @Valid LoginModel loginModel) {
        ResponseResult<Boolean> result = ResponseResult.build();
        logger.info(loginModel.toString());
        userService.login(response, loginModel);
        return result;
    }



}
