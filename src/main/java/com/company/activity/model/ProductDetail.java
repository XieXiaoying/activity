package com.company.activity.model;

import com.company.activity.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetail {
    private int status;
    private int remainSeconds;
    private ProductModel product;
    private User user;
}
