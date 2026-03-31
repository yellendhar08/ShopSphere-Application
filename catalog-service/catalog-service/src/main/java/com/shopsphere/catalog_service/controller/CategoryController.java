package com.shopsphere.catalog_service.controller;

import com.shopsphere.catalog_service.dto.ApiResponse;
import com.shopsphere.catalog_service.entity.Category;
import com.shopsphere.catalog_service.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/catalog/categories")
@RequiredArgsConstructor
public class CategoryController {


    private final CategoryService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories(){
        return ResponseEntity.ok(ApiResponse.success("Categories fetched", service.getAllCategories()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Category>> create(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(
                ApiResponse.success("Category created", service.createCategory(body.get("name")))
        );
    }
}
