package com.shopsphere.catalog_service.repository;

import com.shopsphere.catalog_service.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByIsDeletedFalse(Pageable pageable);

    Page<Product> findByIsDeletedFalseAndNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Product> findByIsDeletedFalseAndCategoryId(Long categoryId, Pageable pageable);

    List<Product> findByIsDeletedFalseAndIsFeaturedTrue();

    Optional<Product> findByIdAndIsDeletedFalse(Long id);

    List<Product> findByIsDeletedFalseAndStockLessThan(Integer stock);
}
