package com.shopsphere.admin_service.feign;

import com.shopsphere.admin_service.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "catalog-service")
public interface CatalogClient {

    @GetMapping("/catalog/products")
    ApiResponse<Object> getAllProducts();

}
