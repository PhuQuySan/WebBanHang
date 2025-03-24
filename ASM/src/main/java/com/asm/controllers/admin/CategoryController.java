package com.asm.controllers.admin;

import com.asm.models.Category;
import com.asm.services.admin.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    
    // Hiển thị trang CRUD (form + danh sách)
    @GetMapping
    public String showCrudPage(Model model) {
        // Nếu chưa có đối tượng category (ví dụ: lần truy cập đầu tiên) thì khởi tạo mới
        if (!model.containsAttribute("category")) {
            model.addAttribute("category", new Category());
        }
        model.addAttribute("categories", categoryService.getAll());
        return "admin/category/crud";  // View mới kết hợp form và list
    }
    
    // Khi nhấn "Thêm mới" (nếu cần, hoặc có thể chỉ dùng nút Làm mới ở form)
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("categories", categoryService.getAll());
        return "admin/category/crud";
    }
    
    // Xử lý lưu (thêm/sửa)
    @PostMapping("/save")
    public String saveCategory(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        try {
            categoryService.save(category);
            redirectAttributes.addFlashAttribute("successMessage", "Lưu danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lưu danh mục thất bại! Vui lòng thử lại.");
        }
        return "redirect:/admin/categories";
    }

    
    // Load dữ liệu cho form khi chọn Sửa
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Category category = categoryService.getById(id);
        model.addAttribute("category", category);
        model.addAttribute("categories", categoryService.getAll());
        return "admin/category/crud";
    }
    
    // Xóa bản ghi
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Integer id) {
        categoryService.delete(id);
        return "redirect:/admin/categories";
    }
}
