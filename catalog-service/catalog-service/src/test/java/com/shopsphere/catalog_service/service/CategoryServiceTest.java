package com.shopsphere.catalog_service.service;

import com.shopsphere.catalog_service.entity.Category;
import com.shopsphere.catalog_service.exception.DuplicateCategoryException;
import com.shopsphere.catalog_service.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository repository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void getAllCategories_success() {
        List<Category> mockList = List.of(
                Category.builder().id(1L).name("Electronics").build(),
                Category.builder().id(2L).name("Clothing").build()
        );

        when(repository.findAll()).thenReturn(mockList);
        List<Category> result = categoryService.getAllCategories();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Electronics", result.get(0).getName());
        verify(repository, times(1)).findAll();
    }


    @Test
    void createCategory_success() {

        String name = "Electronics";
        when(repository.findByName(name)).thenReturn(Optional.empty());

        Category savedCategory = Category.builder()
                .id(1L)
                .name(name)
                .build();

        when(repository.save(any(Category.class))).thenReturn(savedCategory);
        Category result = categoryService.createCategory(name);
        assertNotNull(result);
        assertEquals("Electronics", result.getName());
        verify(repository, times(1)).save(any(Category.class));
    }

    @Test
    void createCategory_duplicate_shouldThrowException() {

        String name = "Electronics";
        Category existing = Category.builder().id(1L).name(name).build();
        when(repository.findByName(name)).thenReturn(Optional.of(existing));
        assertThrows(DuplicateCategoryException.class, () -> categoryService.createCategory(name));
        verify(repository, never()).save(any(Category.class));
    }
}