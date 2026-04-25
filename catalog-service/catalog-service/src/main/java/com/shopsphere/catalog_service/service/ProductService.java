package com.shopsphere.catalog_service.service;

import com.shopsphere.catalog_service.dto.ProductRequest;
import com.shopsphere.catalog_service.dto.ProductResponse;
import com.shopsphere.catalog_service.entity.Category;
import com.shopsphere.catalog_service.entity.Product;
import com.shopsphere.catalog_service.exception.ResourceNotFoundException;
import com.shopsphere.catalog_service.repository.CategoryRepository;
import com.shopsphere.catalog_service.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public Page<ProductResponse> getAllProducts(String search, Long categoryId, int page, int size, String sortBy){
        Pageable pageable =PageRequest.of(page, size, Sort.by(sortBy).ascending());

        Page<Product> products;
        if(search!=null && !search.isEmpty()){
            products = productRepository.findByIsDeletedFalseAndNameContainingIgnoreCase(search, pageable);
        }else if(categoryId!=null){
            products=productRepository.findByIsDeletedFalseAndCategoryId(categoryId, pageable);
        }else {
            products=productRepository.findByIsDeletedFalse(pageable);
        }
        return products.map(this::toResponse);
    }

    public ProductResponse getProductById(Long id){
        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(()->new ResourceNotFoundException("Product not found"));
        return toResponse(product);
    }

    public List<ProductResponse> getFeaturedProducts(){
        return productRepository.findByIsDeletedFalseAndIsFeaturedTrue().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public ProductResponse createProduct(ProductRequest request){
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .isFeatured(request.getIsFeatured())
                .imageUrl(request.getImageUrl())
                .isDeleted(false)
                .category(category)
                .build();
        return toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setStock(Math.max(product.getStock() - quantity, 0));
        return toResponse(productRepository.save(product));
    }

    public ProductResponse updateProduct(Long id, ProductRequest request){
        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(()->new ResourceNotFoundException("Category not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setIsFeatured(request.getIsFeatured());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);
        return toResponse(productRepository.save(product));
    }

    public void deleteProduct(Long id){
        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(()->new ResourceNotFoundException("Product not found"));
        product.setIsDeleted(true);
        productRepository.save(product);
    }

    private ProductResponse toResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .imageUrl(p.getImageUrl())
                .price(p.getPrice())
                .stock(p.getStock())
                .isFeatured(p.getIsFeatured())
                .categoryName(p.getCategory() != null ? p.getCategory().getName() : null)
                .categoryId(p.getCategory() != null ? p.getCategory().getId() : null)
                .createdAt(p.getCreatedAt()).updatedAt(p.getUpdatedAt()).build();
    }
}
