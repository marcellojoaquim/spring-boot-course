package com.projectspring.course.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projectspring.course.entities.Category;


public interface CategoryRepository extends JpaRepository<Category, Long>{

	Optional<Category> findByName(String name);
	
}
