package com.asm.repositories;

import com.asm.models.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Page<Product> findByCategoryId(Integer categoryId, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
}


//package com.asm.repositories;
//
//import com.asm.models.Product;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//
//public interface ProductRepository extends JpaRepository<Product, Integer> {
//    List<Product> findByCategoryId(Integer categoryId);
//    List<Product> findByNameContainingIgnoreCase(String name);
//
//
//}
