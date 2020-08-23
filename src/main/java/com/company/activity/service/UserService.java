package com.company.activity.service;

import com.company.activity.dao.UserDao;
import com.company.activity.domain.User;
import com.company.activity.exception.GlobleException;
import com.company.activity.redis.UserKey;
import com.company.activity.redis.RedisService;
import com.company.activity.utils.MD5Utils;
import com.company.activity.utils.UUIDUtil;
import com.company.activity.model.LoginModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import static com.company.activity.common.enums.ResultStatus.*;

@Service
public class UserService {

    public static final String COOKIE_NAME_TOKEN = "token" ;
    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private RedisService redisService ;



    public User getByToken(HttpServletResponse response , String token) {

        if(StringUtils.isEmpty(token)){
            return null ;
        }
        User user =redisService.get(UserKey.token,token, User.class) ;
        if(user!=null) {
            addCookie(response, token, user);
        }
        return user ;

    }

    public User getByNickName(String nickName) {
        //取缓存
        User user = redisService.get(UserKey.getByNickName, ""+nickName, User.class);
        if(user != null) {
            return user;
        }
        //取数据库
        user = userDao.getByNickname(nickName);
        if(user != null) {
            redisService.set(UserKey.getByNickName, ""+nickName, user);
        }
        return user;
    }


    // http://blog.csdn.net/tTU1EvLDeLFq5btqiK/article/details/78693323
    public boolean updatePassword(String token, String nickName, String formPass) {
        //取user
        User user = getByNickName(nickName);
        if(user == null) {
            throw new GlobleException(MOBILE_NOT_EXIST);
        }
        //更新数据库
        User toBeUpdate = new User();
        toBeUpdate.setNickname(nickName);
        toBeUpdate.setPassword(MD5Utils.passToDBPass(formPass, user.getSalt()));
        userDao.update(toBeUpdate);
        //处理缓存
        redisService.delete(UserKey.getByNickName, ""+nickName);
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(UserKey.token, token, user);
        return true;
    }


    public boolean register(HttpServletResponse response , String userName , String passWord , String salt) {
        User user =  new User();
        user.setNickname(userName);
        String DBPassWord =  MD5Utils.passToDBPass(passWord , salt);
        user.setPassword(DBPassWord);
        user.setRegisterDate(new Date());
        user.setSalt(salt);
        user.setNickname(userName);
        try {
            userDao.insertUser(user);
            User tempUser = userDao.getByNickname(user.getNickname());
            if(tempUser == null){
                return false;
            }
            //生成cookie 将session返回游览器 分布式session
            String token= UUIDUtil.uuid();
            addCookie(response, token, tempUser);
        } catch (Exception e) {
            logger.error("注册失败",e);
            return false;
        }
        return true;
    }

    public boolean login(HttpServletResponse response , LoginModel loginModel) {
        if(loginModel ==null){
            throw  new GlobleException(SYSTEM_ERROR);
        }

        String mobile = loginModel.getMobile();
        String password = loginModel.getPassword();
        User user = getByNickName(mobile);
        if(user == null) {
            throw new GlobleException(MOBILE_NOT_EXIST);
        }

        String dbPass = user.getPassword();
        String saltDb = user.getSalt();
        String calcPass = MD5Utils.passToDBPass(password,saltDb);
        if(!calcPass.equals(dbPass)){
            throw new GlobleException(PASSWORD_ERROR);
        }
        //生成cookie 将session返回游览器 分布式session
        String token = UUIDUtil.uuid();
        addCookie(response, token, user);
        return true ;
    }




    public String createToken(HttpServletResponse response , LoginModel loginModel) {
        if(loginModel ==null){
            throw  new GlobleException(SYSTEM_ERROR);
        }

        String mobile = loginModel.getMobile();
        String password = loginModel.getPassword();
        User user = getByNickName(mobile);
        if(user == null) {
            throw new GlobleException(MOBILE_NOT_EXIST);
        }

        String dbPass = user.getPassword();
        String saltDb = user.getSalt();
        String calcPass = MD5Utils.passToDBPass(password,saltDb);
        if(!calcPass.equals(dbPass)){
            throw new GlobleException(PASSWORD_ERROR);
        }
        //生成cookie 将session返回游览器 分布式session
        String token= UUIDUtil.uuid();
        addCookie(response, token, user);
        return token ;
    }
    private void addCookie(HttpServletResponse response, String token, User user) {
        redisService.set(UserKey.token, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        //设置有效期
        cookie.setMaxAge(UserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}