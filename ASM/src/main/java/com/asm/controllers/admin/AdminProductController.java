package com.asm.controllers.admin;

import com.asm.models.Product;
import com.asm.models.Category;
import com.asm.services.ProductService;
import com.asm.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import java.io.File;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    private static final int PAGE_SIZE = 10;  // Số sản phẩm mỗi trang
    
    /**
     * Hiển thị trang CRUD sản phẩm với phân trang và lọc theo danh mục (nếu có).
     * Nếu model chưa có "product", tạo mới đối tượng Product để form hiển thị.
     * Nếu có tham số catId, chỉ lấy sản phẩm của danh mục đó.
     */
    @GetMapping
    public String showCrudPage(@RequestParam(value="page", defaultValue="0") int page,
                               @RequestParam(value="catId", required=false) Integer catId,
                               Model model) {
        // Nếu form chưa có dữ liệu, tạo đối tượng mới cho form CRUD
        if (!model.containsAttribute("product")) {
            model.addAttribute("product", new Product());
        }
        
        Page<Product> productPage;
        if (catId != null) {
            // Lấy danh sách sản phẩm theo danh mục
            productPage = productService.getProductsByCategory(catId, PageRequest.of(page, PAGE_SIZE));
            model.addAttribute("selectedCategory", catId);
        } else {
            // Lấy tất cả sản phẩm
            productPage = productService.getProducts(PageRequest.of(page, PAGE_SIZE));
        }
        
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        
        // Load danh sách danh mục để hiển thị dropdown lọc
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        
        return "admin/product/crud"; // View: src/main/resources/templates/admin/product/crud.html
    }
    
    /**
     * Load dữ liệu sản phẩm cần chỉnh sửa vào form.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id,
                               @RequestParam(value="page", defaultValue="0") int page,
                               @RequestParam(value="catId", required=false) Integer catId,
                               Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        
        // Nếu có tham số catId, load danh sách sản phẩm theo danh mục; ngược lại load tất cả sản phẩm.
        Page<Product> productPage;
        if (catId != null) {
            productPage = productService.getProductsByCategory(catId, PageRequest.of(page, PAGE_SIZE));
            model.addAttribute("selectedCategory", catId);
        } else {
            productPage = productService.getProducts(PageRequest.of(page, PAGE_SIZE));
        }
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        
        return "admin/product/crud";
    }
    
    /**
     * Nút Clear: Reset form CRUD bằng cách gán đối tượng Product mới.
     */
    @GetMapping("/clear")
    public String clearForm(@RequestParam(value="page", defaultValue="0") int page,
                            @RequestParam(value="catId", required=false) Integer catId,
                            Model model) {
        model.addAttribute("product", new Product());
        
        Page<Product> productPage;
        if (catId != null) {
            productPage = productService.getProductsByCategory(catId, PageRequest.of(page, PAGE_SIZE));
            model.addAttribute("selectedCategory", catId);
        } else {
            productPage = productService.getProducts(PageRequest.of(page, PAGE_SIZE));
        }
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        
        return "admin/product/crud";
    }
    
    /**
     * Xử lý lưu (tạo mới hoặc cập nhật) sản phẩm từ form CRUD.
     */
    @PostMapping("/save")
    public String saveProduct(@ModelAttribute("product") Product product,
                              @RequestParam("imageFile") MultipartFile imageFile,
                              @RequestParam(value="catId", required=false) Integer catId,
                              RedirectAttributes redirectAttributes) {
        // Xử lý upload ảnh nếu có file được chọn
        if (imageFile != null && !imageFile.isEmpty()) {
            System.out.println("File ảnh nhận được: " + imageFile.getOriginalFilename());
            try {
                // Sử dụng thư mục ngoài dự án: "Uploads/Product"
                String currentDir = System.getProperty("user.dir");
                String uploadDirPath = currentDir + "/Uploads/Product";
                
                // Tạo thư mục nếu chưa tồn tại
                File uploadDir = new File(uploadDirPath);
                if (!uploadDir.exists()) {
                    boolean isDirCreated = uploadDir.mkdirs();
                    System.out.println("Tạo thư mục Uploads/Product: " + (isDirCreated ? "Thành công" : "Thất bại"));
                }
                
                // Tạo tên file duy nhất nếu cần (ở đây ta giữ nguyên tên file gốc)
                String fileName = imageFile.getOriginalFilename();
                String filePath = uploadDirPath + "/" + fileName;
                
                // Lưu file vào thư mục Uploads/Product
                imageFile.transferTo(new File(filePath));
                System.out.println("Ảnh đã được lưu tại: " + filePath);
                
                // Gán đường dẫn ảnh đầy đủ vào product, ví dụ: /Uploads/Product/1.png
                product.setImage("/Uploads/Product/" + fileName);
                System.out.println("Đường dẫn ảnh trong CSDL: " + product.getImage());
            } catch (IOException e) {
                System.out.println("Lỗi khi upload ảnh: " + e.getMessage());
                redirectAttributes.addFlashAttribute("error", "Không thể upload ảnh sản phẩm.");
                return "redirect:/admin/products/clear?page=0";
            }
        }
        
        // Xử lý binding danh mục nếu sử dụng cách riêng (nếu cần)
        if (catId != null) {
            Category category = categoryRepository.findById(catId).orElse(null);
            product.setCategory(category);
        }
        
        // Lưu sản phẩm
        productService.save(product);
        return "redirect:/admin/products";
    }


//        @PostMapping("/save")
//    public String saveProduct(@ModelAttribute("product") Product product,
//                              @RequestParam(value="catId", required=false) Integer catId) {
//        // Lưu sản phẩm (có thể xử lý file upload ở đây nếu cần)
//        productService.save(product);
//        // Sau khi lưu, redirect về trang hiển thị với tham số phân trang và lọc (nếu có)
//        if (catId != null) {
//            return "redirect:/admin/products?catId=" + catId;
//        }
//        return "redirect:/admin/products";
//    }
    
    /**
     * Xóa sản phẩm theo ID.
     */
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Integer id,
                                @RequestParam(value="page", defaultValue="0") int page,
                                @RequestParam(value="catId", required=false) Integer catId) {
        productService.delete(id);
        if (catId != null) {
            return "redirect:/admin/products?catId=" + catId + "&page=" + page;
        }
        return "redirect:/admin/products?page=" + page;
    }
}
