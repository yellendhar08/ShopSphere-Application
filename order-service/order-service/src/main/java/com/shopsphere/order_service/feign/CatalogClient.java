package com.shopsphere.order_service.feign;

import com.shopsphere.order_service.dto.ApiResponse;
import com.shopsphere.order_service.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@FeignClient(name = "catalog-service")
public interface CatalogClient {

    @GetMapping("/catalog/products/{id}")
    ApiResponse<ProductResponse> getProductById(@PathVariable Long id);

    @PutMapping("/catalog/products/{id}")
    ApiResponse<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody Map<String, Object> body);

}