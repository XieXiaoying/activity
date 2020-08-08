package com.company.activity.service;

import com.company.activity.dao.ProductDao;
import com.company.activity.model.ProductModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    ProductDao productDao;

    public List<ProductModel> getProductList(){
        return productDao.getProductList();
    }
}
