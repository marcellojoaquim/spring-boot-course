package com.projectspring.course.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectspring.course.entities.Order;
import com.projectspring.course.repositories.OrderRepository;

@Service
public class OrderService {
	
	@Autowired
	private OrderRepository repository;
	
	
	public List<Order> findAll() {
		return repository.findAll();
	}
	
	public Optional<Order> findById(Long id) {
		return repository.findById(id);
	}
}
