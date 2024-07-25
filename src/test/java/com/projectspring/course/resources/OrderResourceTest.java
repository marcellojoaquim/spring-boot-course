package com.projectspring.course.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectspring.course.entities.Order;
import com.projectspring.course.entities.User;
import com.projectspring.course.entities.enums.OrderStatus;
import com.projectspring.course.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderResource.class)
class OrderResourceTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    OrderService service;

    private Order order;
    private Order order1;
    private User user;
    private User user1;

    @BeforeEach
    void setUp() {
        user = new User(null, "Maria Brown", "maria@gmail.com", "988888888", "123456");
        user1 = new User(null, "Joao Brown", "joao@gmail.com", "988888888", "123456");
        order = new Order(null, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, user);
        order1 = new Order(null, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.SHIPPED, user1);

    }

    @Test
    void testFindAllOrders() throws Exception {
        order.setId(1L);
        order1.setId(2L);
        List<Order> orderList = Arrays.asList(order, order1);

        given(service.findAll()).willReturn(orderList);

        mockMvc.perform(get("/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].orderStatus").value(OrderStatus.PAID.toString()))
                .andExpect(jsonPath("$[0].client.name").value("Maria Brown"))
                .andExpect(jsonPath("$[1].id").exists())
                .andExpect(jsonPath("$[1].orderStatus").value(OrderStatus.SHIPPED.toString()))
                .andExpect((ResultMatcher) jsonPath("$.size()", is(orderList.size())));

        verify(service, times(1)).findAll();

    }

    @Test
    void testFindOrderById() throws Exception {
        order.setId(1L);
        long orderId = 1L;
        given(service.findById(orderId)).willReturn(Optional.of(order));

        mockMvc.perform(get("/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderStatus").value(OrderStatus.PAID.toString()));

        verify(service, times(1)).findById(orderId);
    }

}