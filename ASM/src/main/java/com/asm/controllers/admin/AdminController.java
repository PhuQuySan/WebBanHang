package com.asm.controllers.admin;

import com.asm.models.Account;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping
    public String adminHome(HttpSession session, Model model) {
        Account currentUser = (Account) session.getAttribute("currentUser");
        // Kiểm tra role để chắc chắn người dùng là admin
        if (currentUser == null || !"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            return "redirect:/home/index"; // Chuyển hướng nếu không phải admin
        }
        model.addAttribute("admin", currentUser);
        // Forward nội bộ sang /admin/categories để load danh mục trong layout admin
        return "forward:/admin/categories";
    }
}


//package com.asm.controllers.admin;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import com.asm.models.Account;
//
//import jakarta.servlet.http.HttpSession;
//
//@Controller
//@RequestMapping("/admin")
//public class AdminController {
//
//    @GetMapping
//    public String adminHome(HttpSession session, Model model) {
//        Account currentUser = (Account) session.getAttribute("currentUser");
//
//        // Kiểm tra role để chắc chắn người dùng là admin
//        if (!"ADMIN".equals(currentUser.getRole())) {
//            return "redirect:/home/index"; // Chuyển hướng nếu không phải admin
//        }
//
//        model.addAttribute("admin", currentUser);
//        return "admin-layout"; // Trả về trang admin
//        
//    }
//}    