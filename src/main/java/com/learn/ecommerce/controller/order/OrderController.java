package com.learn.ecommerce.controller.order;

import com.learn.ecommerce.entity.LocalUser;
import com.learn.ecommerce.entity.WebOrder;
import com.learn.ecommerce.services.OrderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public Collection<WebOrder> getOrders(@AuthenticationPrincipal LocalUser user) {
        return  orderService.getOrders(user);
    }
}
