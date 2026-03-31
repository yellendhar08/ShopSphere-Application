package com.shopsphere.catalog_service.controller;

import com.shopsphere.catalog_service.dto.ApiResponse;
import com.shopsphere.catalog_service.dto.ProductRequest;
import com.shopsphere.catalog_service.dto.ProductResponse;
import com.shopsphere.catalog_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/catalog/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy){

        return ResponseEntity.ok(
                ApiResponse.success("Products fetched",
                        productService.getAllProducts(search, categoryId, page, size, sortBy))
        );
    }

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getFeatured(){
        return ResponseEntity.ok(ApiResponse.success(
                "Featured products fetched",
                productService.getFeaturedProducts())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id){
        return ResponseEntity.ok(ApiResponse.success(
                "Product fetched",
                productService.getProductById(id))
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@RequestBody ProductRequest request){
        return ResponseEntity.ok(ApiResponse.success(
                "Product created",
                productService.createProduct(request))
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Product updated",
                productService.updateProduct(id, request))
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted", null));
    }

//    @PutMapping("/{id}/stock")
//    public ResponseEntity<ApiResponse<ProductResponse>> updateStock(
//            @PathVariable Long id,
//            @RequestBody Map<String, Integer> body) {
//        return ResponseEntity.ok(ApiResponse.success("Stock updated successfully",
//                productService.updateStock(id, body.get("quantity"))));
//    }
}
