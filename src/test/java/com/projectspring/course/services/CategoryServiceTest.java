package com.projectspring.course.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;

import com.projectspring.course.entities.Category;
import com.projectspring.course.repositories.CategoryRepository;
import com.projectspring.course.services.exceptions.DatabaseException;
import com.projectspring.course.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    
    @Mock
    CategoryRepository repository;
    
    @InjectMocks
    CategoryService service;
    
    private Category category;
    private Category category1;

    @BeforeEach
    void setUp() {
        category = new Category(
                null,
                "Electronics");
    }

    @Test
    void findAll() {
        category1 = new Category(null, "Books");
        given(repository.findAll()).willReturn(List.of(category, category1));

        List<Category> categoryList = service.findAll();

        assertNotNull(categoryList);
        assertEquals(2, categoryList.size());
        assertEquals("Books", categoryList.get(1).getName());
        assertEquals("Electronics", categoryList.get(0).getName());
        verify(repository, times(1)).findAll();

    }

    @Test
    void findById() {
        given(repository.findById(anyLong())).willReturn(Optional.of(category));

        Optional<Category> actual = service.findById(anyLong());

        assertNotNull(actual);
        assertEquals("Electronics", actual.get().getName());
    }

    @Test
    void insert() {
        given(repository.findByName(anyString())).willReturn(Optional.empty());
        given(repository.save(category)).willReturn(category);

        service.insert(category);

        verify(repository, times(1)).findByName(anyString());
        verify(repository, times(1)).save(category);
    }

    @Test
    void insertCategory_IllegalArgumentException(){
        given(repository.findByName(anyString())).willReturn(Optional.of(category));

        IllegalArgumentException actual = assertThrows(IllegalArgumentException.class,
                () -> {service.insert(category);});

        assertEquals("Category exists", actual.getMessage());
    }

    @Test
    void testDeleteCategory() {
        category.setId(123L);
        given(repository.existsById(anyLong())).willReturn(true);
        willDoNothing().given(repository).deleteById(category.getId());

        service.delete(category.getId());

        verify(repository, times(1)).existsById(category.getId());
        verify(repository, times(1)).deleteById(category.getId());
    }

    @Test
    void testDeleteCategory_ResourceNotFoundException(){
        category.setId(1L);

        given(repository.existsById(anyLong())).willReturn(false);

        ResourceNotFoundException actual = assertThrows(ResourceNotFoundException.class, () -> service.delete(category.getId()));

        assertEquals("Resource not found. Id "+category.getId(), actual.getMessage());
        verify(repository, times(0)).deleteById(anyLong());
    }

    @Test
    public void testDeleteCategory_DataIntegrityViolationException() {
        category.setId(3L);

        given(repository.existsById(category.getId())).willReturn(true);
        doThrow(DataIntegrityViolationException.class).when(repository).deleteById(category.getId());

        assertThrows(DatabaseException.class, () -> service.delete(category.getId()));

        verify(repository, times(1)).existsById(category.getId());
    }
}