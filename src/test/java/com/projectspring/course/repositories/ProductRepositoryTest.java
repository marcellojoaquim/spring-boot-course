package com.projectspring.course.repositories;

import com.projectspring.course.entities.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    ProductRepository repository;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product(null, "Smart TV", "Nulla eu imperdiet purus. Maecenas ante.", 2190.0, "");
    }

    @Test
    void testSaveProduct(){
        Product actual = repository.save(product);

        assertNotNull(actual);
        assertTrue(product.getId() > 0);
        assertEquals("Smart TV", actual.getName());
    }

    @Test
    void findProductById(){
        repository.save(product);
        Product actual = repository.findById(product.getId()).get();

        assertTrue(actual.getId()>0);
        assertNotNull(actual.getId());
        assertEquals(product.getId(), actual.getId());
    }

    @Test
    void findAllProducts(){
        Product product1 = new Product();
        repository.save(product);
        repository.save(product1);

        List<Product> productList = repository.findAll();

        assertNotNull(productList);
        assertEquals(2, productList.size());
    }

    @Test
    void deleteProductById(){
        repository.save(product);
        repository.deleteById(product.getId());

        Optional<Product> actual = repository.findById(product.getId());

        assertTrue(actual.isEmpty());
    }
}