package com.asm.services;

import com.asm.models.Product;
import com.asm.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    // Lấy danh sách sản phẩm theo phân trang
    public Page<Product> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
    
    // Lấy danh sách sản phẩm theo danh mục phân trang
    public Page<Product> getProductsByCategory(Integer categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable);
    }
    
    // Lấy thông tin chi tiết của sản phẩm theo ID
    public Product getProductById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }
    
    // Lưu (tạo mới hoặc cập nhật) sản phẩm
    public Product save(Product product) {
        return productRepository.save(product);
    }
    
    // Xóa sản phẩm theo ID
    public void delete(Integer id) {
        productRepository.deleteById(id);
    }
  // Tìm kiếm sản phẩm theo tên (LIKE) và phân trang
  public Page<Product> searchProductsByName(String name, Pageable pageable) {
      return productRepository.findByNameContainingIgnoreCase(name, pageable);
  }
  
}


//package com.asm.services;
//
//import com.asm.models.Product;
//import com.asm.repositories.ProductRepository;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//
//@Service
//public class ProductService {
//    
//    @Autowired
//    private ProductRepository productRepository;
//
//    // Lấy danh sách sản phẩm phân trang toàn bộ
//    public Page<Product> getAllProducts(Pageable pageable) {
//        return productRepository.findAll(pageable);
//    }
//
//    // Lấy danh sách sản phẩm theo danh mục phân trang
//    public Page<Product> getProductsByCategory(Integer categoryId, Pageable pageable) {
//        return productRepository.findByCategoryId(categoryId, pageable);
//    }
//
//    // Lấy thông tin chi tiết của sản phẩm
//    public Product getProductById(Integer id) {
//        return productRepository.findById(id).orElse(null);
//    }
//    
//    // Tìm kiếm sản phẩm theo tên (LIKE) và phân trang
//    public Page<Product> searchProductsByName(String name, Pageable pageable) {
//        return productRepository.findByNameContainingIgnoreCase(name, pageable);
//    }
//
//
//}
//




//package com.asm.services;
//
//import com.asm.models.Product;
//import com.asm.repositories.ProductRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class ProductService {
//    @Autowired
//    private ProductRepository productRepository;
//
//    public List<Product> getAllProducts() {
//        return productRepository.findAll();
//    }
//
//    public List<Product> getProductsByCategory(Integer categoryId) {
//        return productRepository.findByCategoryId(categoryId);
//    }
//
//    public Product getProductById(Integer id) {
//        return productRepository.findById(id).orElse(null);
//    }
//    
//    // Phương thức tìm kiếm sản phẩm theo tên theo kiểu LIKE
//    public List<Product> searchProductsByName(String name) {
//        return productRepository.findByNameContainingIgnoreCase(name);
//    }
//    
//}
