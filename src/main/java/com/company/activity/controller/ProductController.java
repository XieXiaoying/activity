package com.company.activity.controller;

import com.company.activity.access.AccessLimit;
import com.company.activity.common.resultbean.ResponseResult;
import com.company.activity.domain.User;
import com.company.activity.model.ProductDetail;
import com.company.activity.model.ProductModel;
import com.company.activity.redis.ProductKey;
import com.company.activity.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController extends PageController {

    @Autowired
    ProductService productService;

    @RequestMapping(value="/list", produces="text/html")
    @ResponseBody
    public String list(HttpServletRequest request, HttpServletResponse response, Model model, User user) {
        model.addAttribute("user", user);
        List<ProductModel> goodsList = productService.getProductList();
        model.addAttribute("productList", goodsList);
        return render(request, response, model, "productList", ProductKey.productList,"");
    }

    @RequestMapping(value="/detail/{productsId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult<ProductDetail> detail(User user,
                                                @PathVariable("productsId")long productsId) {
        ResponseResult<ProductDetail> result = ResponseResult.build();
        ProductModel product = productService.getProductById(productsId);
        long startAt = product.getStartDate().getTime();
        long endAt = product.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int status = 0;
        int remainSeconds = 0;
        if(now < startAt) {//秒杀还没开始，倒计时
            status = 0;
            remainSeconds = (int)((startAt - now )/1000);
        }else  if(now > endAt){//秒杀已经结束
            status = 2;
            remainSeconds = -1;
        }else {//秒杀进行中
            status = 1;
            remainSeconds = 0;
        }
        ProductDetail productDetail = new ProductDetail();
        productDetail.setProduct(product);
        productDetail.setUser(user);
        productDetail.setRemainSeconds(remainSeconds);
        productDetail.setStatus(status);
        result.setData(productDetail);
        return result;
    }
}
