package com.projectspring.course.repositories;

import com.projectspring.course.entities.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository repository;

    private Category category;


    @BeforeEach
    void setUp() {
        category = new Category(
                "Books"
        );
    }

    @Test
    void testSaveCategory(){
        Category cat = repository.save(category);

        assertNotNull(cat);
        assertTrue(cat.getId() > 0);
        assertEquals("Books", cat.getName());
    }

    @Test
    void findCategoryByName() {
        repository.save(category);
        Category actual = repository.findByName(category.getName()).get();

        assertNotNull(actual);
        assertEquals(category.getName(), actual.getName());
    }

    @Test
    void testFindAllCategory(){
        Category category1 = new Category(
                "Electronics"
        );
        repository.save(category);
        repository.save(category1);

        List<Category> categories = repository.findAll();

        assertNotNull(categories);
        assertEquals(2, categories.size());
    }

    @Test
    void testFindCategoryById(){
        repository.save(category);
        Category actual = repository.findById(category.getId()).get();

        assertTrue(actual.getId()>0);
        assertNotNull(actual.getId());
        assertEquals(category.getId(), actual.getId());
    }

    @Test
    void testDeleteCategory(){
        repository.save(category);
        repository.deleteById(category.getId());

        Optional<Category> actual = repository.findById(category.getId());

        assertTrue(actual.isEmpty());
    }
}