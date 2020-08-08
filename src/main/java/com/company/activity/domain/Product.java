package com.company.activity.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Product{
    private Long id;
    private String name;
    private String title;
    private String img;
    private String detail;
    private Double price;
    private Integer stock;
}
