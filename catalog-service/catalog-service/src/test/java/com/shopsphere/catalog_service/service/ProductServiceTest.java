package com.shopsphere.catalog_service.service;
import com.shopsphere.catalog_service.dto.ProductRequest;
import com.shopsphere.catalog_service.dto.ProductResponse;
import com.shopsphere.catalog_service.entity.Category;
import com.shopsphere.catalog_service.entity.Product;
import com.shopsphere.catalog_service.exception.ResourceNotFoundException;
import com.shopsphere.catalog_service.repository.CategoryRepository;
import com.shopsphere.catalog_service.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void getAllProducts_withSearch_success() {
        Pageable pageable = PageRequest.of(0, 5);

        Product product = Product.builder()
                .id(1L).name("Laptop").isDeleted(false)
                .build();

        Page<Product> page = new PageImpl<>(List.of(product));

        when(productRepository.findByIsDeletedFalseAndNameContainingIgnoreCase(eq("lap"), any()))
                .thenReturn(page);

        Page<ProductResponse> result = productService.getAllProducts("lap", null, 0, 5, "name");

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAllProducts_withCategory_success() {
        Product product = Product.builder().id(1L).isDeleted(false).build();
        Page<Product> page = new PageImpl<>(List.of(product));

        when(productRepository.findByIsDeletedFalseAndCategoryId(eq(1L), any()))
                .thenReturn(page);

        Page<ProductResponse> result = productService.getAllProducts(null, 1L, 0, 5, "name");

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAllProducts_default_success() {
        Product product = Product.builder().id(1L).isDeleted(false).build();
        Page<Product> page = new PageImpl<>(List.of(product));

        when(productRepository.findByIsDeletedFalse(any()))
                .thenReturn(page);

        Page<ProductResponse> result = productService.getAllProducts(null, null, 0, 5, "name");
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getProductById_success() {
        Product product = Product.builder().id(1L).isDeleted(false).build();

        when(productRepository.findByIdAndIsDeletedFalse(1L))
                .thenReturn(Optional.of(product));

        ProductResponse response = productService.getProductById(1L);
        assertNotNull(response);
    }

    @Test
    void getProductById_notFound() {
        when(productRepository.findByIdAndIsDeletedFalse(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    void getFeaturedProducts_success() {
        Product product = Product.builder().id(1L).isFeatured(true).build();

        when(productRepository.findByIsDeletedFalseAndIsFeaturedTrue())
                .thenReturn(List.of(product));

        List<ProductResponse> result = productService.getFeaturedProducts();

        assertEquals(1, result.size());
    }

    @Test
    void createProduct_success() {
        ProductRequest request = new ProductRequest();
        request.setCategoryId(1L);

        Category category = Category.builder().id(1L).name("Electronics").build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        ProductResponse response = productService.createProduct(request);

        assertNotNull(response);
    }

    @Test
    void createProduct_categoryNotFound() {
        ProductRequest request = new ProductRequest();
        request.setCategoryId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.createProduct(request));
    }

    @Test
    void updateStock_success() {
        Product product = Product.builder().id(1L).stock(10).build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ProductResponse response = productService.updateStock(1L, 3);
        assertEquals(7, response.getStock());
    }

    @Test
    void updateStock_notFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.updateStock(1L, 3));
    }

    @Test
    void updateProduct_success() {
        ProductRequest request = new ProductRequest();
        request.setCategoryId(1L);

        Product product = Product.builder().id(1L).isDeleted(false).build();
        Category category = Category.builder().id(1L).build();

        when(productRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ProductResponse response = productService.updateProduct(1L, request);

        assertNotNull(response);
    }

    @Test
    void updateProduct_productNotFound() {
        ProductRequest request = new ProductRequest();

        when(productRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.updateProduct(1L, request));
    }

    @Test
    void deleteProduct_success() {
        Product product = Product.builder().id(1L).isDeleted(false).build();

        when(productRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        assertTrue(product.getIsDeleted());
        verify(productRepository).save(product);
    }

    @Test
    void deleteProduct_notFound() {

        when(productRepository.findByIdAndIsDeletedFalse(1L))
                .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(1L));
    }
}