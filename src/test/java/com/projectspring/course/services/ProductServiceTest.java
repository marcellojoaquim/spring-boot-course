package com.projectspring.course.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projectspring.course.entities.Product;
import com.projectspring.course.repositories.ProductRepository;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
	
	@InjectMocks
	ProductService service;
	
	@Mock
	ProductRepository repository;
	
	Product product;
	
	@BeforeEach
	public void setUp() {
		product = new Product(1L, "Tv Test", "Dado de teste", 1200.00, "url de teste");
	}
	
	@Test
	void deveBuscarTodosProdutos() {
		when(repository.findAll()).thenReturn(Collections.singletonList(product));
		List<Product> products = service.findAll();
		
		assertEquals(Collections.singletonList(product), products);
		verify(repository).findById(product.getId());
		verifyNoMoreInteractions(repository);
		
	}
	/*
	@Test
	void naoDeveChamarRepositoryCasoSemParamId() {
		final 
	}
	*/
}
