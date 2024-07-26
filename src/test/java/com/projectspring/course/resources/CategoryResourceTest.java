package com.projectspring.course.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectspring.course.entities.Category;
import com.projectspring.course.services.CategoryService;
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

import static org.hamcrest.CoreMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryResource.class)
class CategoryResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CategoryService service;

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category(
                1L,
                "Electronics"
        );
    }

    @Test
    void testFindAllCategories() throws Exception {
        Category category1 = new Category(
                2L,
                "Books"
        );
        List<Category> categoryList = Arrays.asList(category, category1);

        given(service.findAll()).willReturn(categoryList);
        mockMvc.perform(get("/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Electronics"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value(categoryList.get(1).getName()))
                .andExpect((ResultMatcher) jsonPath("$.size()", is(categoryList.size())));

        verify(service, times(1)).findAll();
    }

    @Test
    void testFindCategoriesById() throws Exception {
        long catId = 1L;
        given(service.findById(catId)).willReturn(Optional.of(category));

        mockMvc.perform(get("/categories/{id}", catId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(category.getName()));

        verify(service,times(1)).findById(catId);
    }

    @Test
    void testInsertCategory() throws Exception {

        doNothing().when(service).insert(any(Category.class));

        mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(category)))
                .andExpect(status().isOk())
                .andExpect(content().string("Category name exists"));

        verify(service, times(1)).insert(any(Category.class));
    }

    @Test
    void testInsertCategoryFailure() throws Exception {
        String msg = "Category exists";

        doThrow(new IllegalArgumentException(msg))
                .when(service).insert(any(Category.class));

        mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(category)))
                .andExpect(status().isBadRequest());
        verify(service, times(1)).insert(any(Category.class));
    }

    @Test
    void testDeleteCategory() throws Exception {
        long catId = 1L;
        willDoNothing().given(service).delete(catId);

        ResultActions response = mockMvc.perform(delete("/categories/{id}", catId));

        response.andExpect(status().isNoContent());
    }
}