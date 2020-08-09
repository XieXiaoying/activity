package com.company.activity.model;

import com.company.activity.domain.OrderInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetail {
    private ProductModel productModel;
    private OrderInfo orderInfo;
}
