package com.shopsphere.catalog_service.service;

import com.shopsphere.catalog_service.entity.Category;
import com.shopsphere.catalog_service.exception.DuplicateCategoryException;
import com.shopsphere.catalog_service.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;

    public List<Category> getAllCategories(){
        return repository.findAll();
    }

    public Category createCategory(String name){
        if(repository.findByName(name).isPresent()){
            throw new DuplicateCategoryException("Category already exists");
        }
        return repository.save(Category.builder().name(name).build());
    }
}
