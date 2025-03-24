package com.asm.controllers;

import com.asm.models.Account;
import com.asm.models.Orders;
import com.asm.services.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class OrderHistoryController {

    @Autowired
    private OrderService orderService;
    
    @GetMapping("/orders/history")
    public String orderHistory(HttpSession session, Model model) {
        // Lấy thông tin người dùng từ session
        Account currentUser = (Account) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        
        // Kiểm tra role: giả sử role "0" hoặc "ROLE_USER" cho người dùng thông thường
        if (!"0".equals(currentUser.getRole()) && !"ROLE_USER".equalsIgnoreCase(currentUser.getRole())) {
            // Nếu không phải role người dùng, chuyển hướng về trang chủ
            return "redirect:/";
        }
        
        // Lấy lịch sử đơn hàng của người dùng
        List<Orders> orders = orderService.getOrdersByAccount(currentUser);
        model.addAttribute("orders", orders);
        
        return "orders/order-history"; // View: src/main/resources/templates/user/order-history.html
    }
}