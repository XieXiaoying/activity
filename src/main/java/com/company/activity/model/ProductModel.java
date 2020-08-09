package com.company.activity.model;

import com.company.activity.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductModel extends Product {
    private Double currentPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
}
