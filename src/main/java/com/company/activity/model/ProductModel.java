package com.company.activity.model;

import com.company.activity.domain.Product;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ProductModel extends Product {
    private Double currentPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
}
