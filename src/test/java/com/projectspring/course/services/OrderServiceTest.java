package com.projectspring.course.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;

import com.projectspring.course.entities.Order;
import com.projectspring.course.entities.User;
import com.projectspring.course.entities.enums.OrderStatus;
import com.projectspring.course.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrderRepository repository;

    @InjectMocks
    OrderService service;

    private Order order;
    private Order order1;
    private User u1;

    @BeforeEach
    void setUp() {
        order = new Order(
            null,
            Instant.parse("2019-06-20T19:53:07Z"),
            OrderStatus.PAID,
            u1);
    }

    @Test
    void findAll() {
        order1 = new Order(
                null,
                Instant.parse("2019-07-22T15:21:22Z"),
                OrderStatus.WAITING_PAYMENT,
                u1);
        given(repository.findAll()).willReturn(List.of(order, order1));

        List<Order> orderList = service.findAll();

        assertNotNull(orderList);
        assertEquals(2, orderList.size());
        assertEquals("WAITING_PAYMENT", orderList.get(1).getOrderStatus().toString());
        verify(repository, times(1)).findAll();
    }

    @Test
    void findById() {
        given(repository.findById(anyLong())).willReturn(Optional.of(order));

        Optional<Order> actual = service.findById(1L);

        assertNotNull(actual);
        assertEquals("2019-06-20T19:53:07Z", order.getMoment().toString());
        verify(repository, times(1)).findById(1L);
    }
}