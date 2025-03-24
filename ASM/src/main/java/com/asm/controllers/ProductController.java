package com.asm.controllers;

import com.asm.models.Product;
import com.asm.repositories.CategoryRepository;
import com.asm.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private CategoryRepository categoryRepository;

    private static final int PAGE_SIZE = 9; // Số sản phẩm mỗi trang

    @GetMapping("/")
    public String home(Model model) {
    	 return "layouts/home";
    }
    
    @GetMapping("/home/index")
    public String listProducts(Model model,
                               @RequestParam(value = "page", defaultValue = "0") int page) {
        // Lấy trang sản phẩm phân trang
        Page<Product> productPage = productService.getProducts(PageRequest.of(page, PAGE_SIZE));
        List<Product> products = productPage.getContent();
        List<?> categories = categoryRepository.findAll();
        
        // Thêm dữ liệu vào model
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        
        // Các view (index.html) sẽ sử dụng main layout, trong đó có phân trang chung.
        return "product/index";
    }
    
    @GetMapping("/product/list-by-category/{categoryId}")
    public String listProductsByCategory(@PathVariable Integer categoryId,
                                         Model model,
                                         @RequestParam(value = "page", defaultValue = "0") int page) {
        Page<Product> productPage = productService.getProductsByCategory(categoryId, PageRequest.of(page, PAGE_SIZE));
        List<Product> products = productPage.getContent();
        List<?> categories = categoryRepository.findAll();
        
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategory", categoryId);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        
        return "product/index";
    }
    
    @GetMapping("/product/detail/{id}")
    public String productDetail(@PathVariable Integer id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "product/detail";
    }
   
    @GetMapping("/product/search")
    public String searchProducts(@RequestParam("name") String name,
                                 Model model,
                                 @RequestParam(value = "page", defaultValue = "0") int page) {
        Page<Product> productPage = productService.searchProductsByName(name, PageRequest.of(page, PAGE_SIZE));
        List<Product> products = productPage.getContent();
        
        model.addAttribute("products", products);
        model.addAttribute("nametim", name);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        
        return "product/search";
    }
}

//package com.asm.controllers;
//
//import com.asm.models.Product;
//import com.asm.models.Category;
//import com.asm.repositories.CategoryRepository;
//import com.asm.services.ProductService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@Controller
//public class ProductController {
//
//    @Autowired
//    private ProductService productService;
//    
//    @Autowired
//    private CategoryRepository categoryRepository;
//
//    private static final int PAGE_SIZE = 9; // Số sản phẩm mỗi trang
//
//    @GetMapping("/")
//    public String home(Model model) {
//        return "redirect:/home/index";
//    }
//
//    @GetMapping("/home/index")
//    public String listProducts(Model model,
//                               @RequestParam(value = "page", defaultValue = "0") int page) {
//        Page<Product> productPage = productService.getAllProducts(PageRequest.of(page, PAGE_SIZE));
//        List<Category> categories = categoryRepository.findAll();
//
//        model.addAttribute("products", productPage.getContent());
//        model.addAttribute("categories", categories);
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", productPage.getTotalPages());
//
//        return "product/index";
//    }
//
//    @GetMapping("/product/list-by-category/{categoryId}")
//    public String listProductsByCategory(@PathVariable Integer categoryId,
//                                         Model model,
//                                         @RequestParam(value = "page", defaultValue = "0") int page) {
//        Page<Product> productPage = productService.getProductsByCategory(categoryId, PageRequest.of(page, PAGE_SIZE));
//        List<Category> categories = categoryRepository.findAll();
//
//        model.addAttribute("products", productPage.getContent());
//        model.addAttribute("categories", categories);
//        model.addAttribute("selectedCategory", categoryId);
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", productPage.getTotalPages());
//
//        return "product/index";
//    }
//
//    @GetMapping("/product/detail/{id}")
//    public String productDetail(@PathVariable Integer id, Model model) {
//        Product product = productService.getProductById(id);
//        model.addAttribute("product", product);
//        return "product/detail";
//    }
//   
//    @GetMapping("/product/search")
//    public String searchProducts(@RequestParam("name") String name,
//                                 Model model,
//                                 @RequestParam(value = "page", defaultValue = "0") int page) {
//        Page<Product> productPage = productService.searchProductsByName(name, PageRequest.of(page, PAGE_SIZE));
//        
//        model.addAttribute("products", productPage.getContent());
//        model.addAttribute("nametim", name);
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", productPage.getTotalPages());
//        
//        return "product/search";
//    }
//}




//package com.asm.controllers;
//
//import com.asm.models.Category;
//import com.asm.models.Product;
//import com.asm.repositories.CategoryRepository;
//import com.asm.services.ProductService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.util.List;
//@ControllerAdvice
//@Controller
//public class ProductController {
//
//    @Autowired
//    private ProductService productService;
//
//    @Autowired
//    private CategoryRepository categoryRepository;
//
//    @GetMapping("/")
//    public String homee(Model model) {
//        return "redirect:/home/index";
//    }
//    
//    
//    @GetMapping("/home/index")
//    public String listProducts(Model model) {
//        List<Product> products = productService.getAllProducts();
//        List<Category> categories = categoryRepository.findAll();
//        model.addAttribute("products", products);
//        model.addAttribute("categories", categories);
//        return "product/index";
//    }
//
//    @GetMapping("/product/list-by-category/{categoryId}")
//    public String listProductsByCategory(@PathVariable Integer categoryId, Model model) {
//        List<Product> products = productService.getProductsByCategory(categoryId);
//        List<Category> categories = categoryRepository.findAll();
//        model.addAttribute("products", products);
//        model.addAttribute("categories", categories);
//        model.addAttribute("selectedCategory", categoryId);
//        return "product/index";
//    }
//
//    @GetMapping("/product/detail/{id}")
//    public String productDetail(@PathVariable Integer id, Model model) {
//        Product product = productService.getProductById(id);
//        model.addAttribute("product", product);
//        return "product/detail";
//    }
//   
//    @GetMapping("/product/search")
//    public String searchProducts(@RequestParam("name") String name, Model model) {
//        List<Product> products = productService.searchProductsByName(name);
//        if (products.isEmpty()) {
//            model.addAttribute("error", "Không tìm thấy sản phẩm nào với từ khóa: " + name);
//        }
//        model.addAttribute("nametim", name);
//        model.addAttribute("products", products);
//        return "product/search"; // View hiển thị kết quả tìm kiếm
//    }
////    // Mapping mới để tìm kiếm sản phẩm theo id
////    @GetMapping("/product/search")
////    public String searchProduct(@RequestParam("name") Integer name, Model model) {
////        Product product = productService.getProductById(name);
////        if (product == null) {
////            model.addAttribute("error", "Không tìm thấy sản phẩm với tên sản phẩm: " + name);
////        } else {
////            model.addAttribute("product", product);
////        }
////        return "product/search";
////    }
//    
//    
//}