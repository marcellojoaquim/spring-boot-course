package com.projectspring.course.repositories;

import com.projectspring.course.entities.Category;
import com.projectspring.course.entities.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository repository;

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
    }

    @Test
    void testFindOrderById(){
        repository.save(order);
        Order actual = repository.findById(order.getId()).get();

        assertTrue(actual.getId()>0);
        assertNotNull(actual.getId());
        assertEquals(order.getId(), actual.getId());
    }

    @Test
    void testFindAllOrders(){
        Order order1 = new Order();
        repository.save(order1);
        repository.save(order);

        List<Order> orders = repository.findAll();

        assertNotNull(orders);
        assertEquals(2, orders.size());
    }

}