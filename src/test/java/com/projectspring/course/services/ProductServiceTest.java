package com.projectspring.course.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.projectspring.course.services.exceptions.DatabaseException;
import com.projectspring.course.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projectspring.course.entities.Product;
import com.projectspring.course.repositories.ProductRepository;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
	
	@InjectMocks
	ProductService service;
	
	@Mock
	ProductRepository repository;
	
	Product product;
	
	@BeforeEach
	public void setUp() {

		product = new Product(
				1L,
				"Tv Test",
				"Dado de teste",
				1200.00,
				"test.com");
	}
	
	@Test
	void findAllProducts() {
		Product p4 = new Product(
				null,
				"PC Gamer",
				"Donec aliquet odio ac rhoncus cursus.",
				1200.0,
				"");
		given(repository.findAll()).willReturn(List.of(product, p4));

		List<Product> products = service.findAll();

		assertNotNull(products);
		assertEquals(2, products.size());
		assertEquals("PC Gamer", products.get(1).getName());
		verify(repository, times(1)).findAll();
	}

	@Test
	void testFindAllProductEmptyLIst(){
		given(repository.findAll()).willReturn(Collections.emptyList());
		List<Product> products = service.findAll();

		assertTrue(products.isEmpty());
		assertTrue(products.size() < 1);
	}

	@Test
	void testFindProductById(){
		given(repository.findById(anyLong())).willReturn(Optional.of(product));

		Optional<Product> actual = service.findById(1L);

		assertNotNull(actual);
		assertEquals("Tv Test", actual.get().getName());
	}

	@Test
	void testInsertProduct(){
		given(repository.save(product)).willReturn(product);

		Product actual = service.insert(product);

		assertNotNull(actual);
		assertEquals("Tv Test", actual.getName());
		assertEquals("Dado de teste", actual.getDescription());
		verify(repository, times(1)).save(product);
	}

	@Test
	void testUpdateProduct() {

		Product updatedProduct = new Product();
		updatedProduct.setId(1L);
		updatedProduct.setName(product.getName());
		updatedProduct.setDescription(product.getDescription());
		updatedProduct.setImgUrl(product.getImgUrl());

		given(repository.getReferenceById(anyLong())).willReturn(product);
		given(repository.save(any(Product.class))).willReturn(updatedProduct);

		Product actual = service.update(1L, updatedProduct);

		assertNotNull(actual.getId());
		assertEquals("Tv Test", actual.getName());
		assertEquals("test.com", actual.getImgUrl());
		verify(repository, times(1)).getReferenceById(anyLong());
		verify(repository, times(1)).save(any(Product.class));
	}

	@Test
	void testDeleteProduct(){
		product.setId(1L);
		given(repository.existsById(anyLong())).willReturn(true);
		willDoNothing().given(repository).deleteById(product.getId());

		service.delete(product.getId());

		verify(repository, times(1)).deleteById(product.getId());
	}

	@Test
	public void testDeleteProduct_ResourceNotFoundException() {
		Long productId = 2L;

		given(repository.existsById(productId)).willReturn(false);

		assertThrows(ResourceNotFoundException.class, () -> service.delete(productId));
	}

	@Test
	public void testDeleteProduct_DataIntegrityViolationException() {
		Long productId = 3L;

		given(repository.existsById(productId)).willReturn(true);
		doThrow(DataIntegrityViolationException.class).when(repository).deleteById(productId);

		assertThrows(DatabaseException.class, () -> service.delete(productId));
	}

	@Test
	public void testUpdateProduct_ResourceNotFoundException() {
		String expectedMessage = "Resource not found. Id ";
		Long productId = 2L;

		given(repository.getReferenceById(anyLong())).willThrow(EntityNotFoundException.class);

		ResourceNotFoundException actual = assertThrows(ResourceNotFoundException.class,
				() -> service.update(productId, new Product()));

		assertEquals(expectedMessage+productId, actual.getMessage());
		verify(repository, times(1)).getReferenceById(anyLong());
	}


}
