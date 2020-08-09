package com.company.activity.rabbitmq;

import com.company.activity.domain.User;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ActivityMessage {
    private User user;
    private long productsId;
}
