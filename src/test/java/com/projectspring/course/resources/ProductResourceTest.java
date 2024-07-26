package com.projectspring.course.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectspring.course.entities.Product;
import com.projectspring.course.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ProductResource.class)
class ProductResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    ProductService service;

    private Product product;
    private Product product1;

    @BeforeEach
    void setUp() {
        product = new Product(
                null,
                "Smart TV",
                "Nulla eu imperdiet purus. Maecenas ante.",
                2190.0,
                "urltest.com");
    }

    @Test
    void testFindAllProducts() throws Exception {
        product1 = new Product(
                null,
                "Macbook Pro",
                "Nam eleifend maximus tortor, at mollis.",
                1250.0, "");
        product1.setId(2L);
        product.setId(1L);

        List<Product> productList = Arrays.asList(product, product1);

        given(service.findAll()).willReturn(productList);

        mockMvc.perform(get("/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Smart TV"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Macbook Pro"))
                .andExpect((ResultMatcher) jsonPath("$.size()", is(productList.size())));

    }

    @Test
    void testFindProductById() throws Exception {
        long catId = 1L;
        given(service.findById(catId)).willReturn(Optional.of(product));

        mockMvc.perform(get("/products/{id}", catId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Smart TV"));

        verify(service,times(1)).findById(catId);
    }

    @Test
    void testInsertProduct() throws Exception {
        given(service.insert(any(Product.class))).willReturn(product);

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(product)))
                .andExpect(status().isCreated());

        verify(service, times(1)).insert(any(Product.class));
    }

    @Test
    void testDeleteProduct() throws Exception {
        long catId = 1L;
        willDoNothing().given(service).delete(catId);

        mockMvc.perform(delete("/products/{id}", catId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testUpdateProduct() throws Exception{
        long pId = 1L;
        Product updatedProduct = new Product(
                null,
                "Macbook Pro",
                "Nam eleifend maximus tortor, at mollis.",
                1250.0,
                "urltest.com");

        given(service.findById(pId)).willReturn(Optional.of(product));
        given(service.update(pId, product)).willReturn(updatedProduct);

        ResultActions response = mockMvc.perform(put("/products/{id}", pId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updatedProduct)));

        response.andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.name", is(updatedProduct.getName())))
                .andExpect(jsonPath("$.price").value(1250.0))
                .andExpect(jsonPath("$.imgUrl").value(product.getImgUrl()))
                .andExpect((ResultMatcher) jsonPath("$.description", is(updatedProduct.getDescription())));
    }
}