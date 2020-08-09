package com.company.activity.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfo {
    private Long id;
    private Long userId;
    private Long productsId;
    private Long  deliveryAddrId;
    private String productsName;
    private Integer productsCount;
    private Double productsPrice;
    private Integer orderChannel;
    private Integer status;
    private Date createDate;
    private Date payDate;
}