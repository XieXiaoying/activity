package com.company.activity.controller;

import com.company.activity.redis.KeyPrefix;
import com.company.activity.redis.RedisService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

@Controller
public class PageController {

    //加一个配置项
    @Value("${pageCache.enbale}")
    private boolean pageCacheEnable;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    RedisService redisService;

    public String render(HttpServletRequest request, HttpServletResponse response, Model model, String tplName, KeyPrefix prefix, String key) {
        // 如果设置了不允许页面缓存，则直接返回页面
        if(!pageCacheEnable) {
            return tplName;
        }
        //取缓存
        String html = redisService.get(prefix, key, String.class);
        if(!StringUtils.isEmpty(html)) {
            out(response, html);
            return null;
        }
        //缓存中没有页面的情况，手动渲染
        WebContext ctx = new WebContext(request,response,
                request.getServletContext(),request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process(tplName, ctx);
        if(!StringUtils.isEmpty(html)) {
            redisService.set(prefix, key, html);
        }
        out(response, html);
        return null;
    }
    public static void out(HttpServletResponse res, String html){
        res.setContentType("text/html");
        res.setCharacterEncoding("UTF-8");
        try{
            OutputStream out = res.getOutputStream();
            out.write(html.getBytes("UTF-8"));
            out.flush();
            out.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
