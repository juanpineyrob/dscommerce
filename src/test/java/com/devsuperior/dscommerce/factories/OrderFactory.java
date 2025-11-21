package com.devsuperior.dscommerce.factories;

import com.devsuperior.dscommerce.entities.Order;
import com.devsuperior.dscommerce.entities.OrderStatus;
import com.devsuperior.dscommerce.entities.Payment;
import com.devsuperior.dscommerce.entities.User;

import java.time.Instant;

public class OrderFactory {
    public static Order createOrder() {
        Instant instant = Instant.now();

        OrderStatus orderStatus = OrderStatus.WAITING_PAYMENT;

        User user = UserFactory.createRoleClientUser("ex@ds.com");

        Payment payment = new Payment();

        return new Order(1L, instant, orderStatus, user, payment);
    }
}
