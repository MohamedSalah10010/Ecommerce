package com.learn.ecommerce.services;

import com.learn.ecommerce.entity.LocalUser;
import com.learn.ecommerce.entity.WebOrder;
import com.learn.ecommerce.repository.WebOrderRepo;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class OrderService {

    private WebOrderRepo webOrderRepo;

    public OrderService(WebOrderRepo webOrderRepo) {
        this.webOrderRepo = webOrderRepo;
    }

    public Collection<WebOrder> getOrders(LocalUser user) {
        return webOrderRepo.findByUser(user);
    }
}

