package com.projectspring.course.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectspring.course.entities.Category;
import com.projectspring.course.repositories.CategoryRepository;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository repository;
	
	
	public List<Category> findAll() {
		return repository.findAll();
	}
	
	public Optional<Category> findById(Long id) {
		return repository.findById(id);
	}
}
