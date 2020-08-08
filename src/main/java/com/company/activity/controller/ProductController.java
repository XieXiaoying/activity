package com.company.activity.controller;

import com.company.activity.domain.User;
import com.company.activity.model.ProductModel;
import com.company.activity.redis.ProductKey;
import com.company.activity.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
}
