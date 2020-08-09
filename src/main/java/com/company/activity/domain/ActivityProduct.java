package com.company.activity.domain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityProduct {
    private Long id;
    private Long productsId;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
}
