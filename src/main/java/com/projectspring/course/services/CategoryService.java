package com.projectspring.course.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.projectspring.course.entities.Category;
import com.projectspring.course.repositories.CategoryRepository;
import com.projectspring.course.services.exceptions.DatabaseException;
import com.projectspring.course.services.exceptions.ResourceNotFoundException;

import jakarta.transaction.Transactional;

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
	
	@Transactional
	public void insert(Category obj) {
		Optional<Category> existsCategory = repository.findByName(obj.getName());
		if(existsCategory.isPresent()) {
			throw new IllegalArgumentException("Category exists");
		}
		repository.save(obj);
	}
	
	public void delete(Long id) {
		try {
			if(repository.existsById(id)) {
				repository.deleteById(id);
			} else {
				throw new ResourceNotFoundException(id);
			}
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException(e.getMessage());
		}
	}
}
