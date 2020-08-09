package com.company.activity.service;

import com.company.activity.dao.ProductDao;
import com.company.activity.domain.ActivityProduct;
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

    public ProductModel getProductById(long id){
        return productDao.getProductById(id);
    }

    public boolean reduceStock(ProductModel product) {
        ActivityProduct acProduct = new ActivityProduct();
        acProduct.setProductsId(product.getId());
        int stock = productDao.reduceStock(product);
        return stock > 0;
    }

}
