package com.asm.services.admin;

import com.asm.models.Category;
import com.asm.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }
    
    public Category getById(Integer id) {
        return categoryRepository.findById(id).orElse(null);
    }
    
    public Category save(Category category) {
        return categoryRepository.save(category);
    }
    
    public void delete(Integer id) {
        categoryRepository.deleteById(id);
    }
}
