package com.asm.controllers;

import com.asm.models.Account;
import com.asm.services.AccountService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private AccountService accountService;

    // Hiển thị form đăng nhập
    @GetMapping("auth/login")
    public String showLoginForm() {
        return "auth/login";
    }

    // Đăng nhập
    @PostMapping("auth/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {
        Optional<Account> optionalAccount = accountService.findByUsername(username);

        if (optionalAccount.isPresent() && accountService.login(username, password)) {
            Account currentUser = optionalAccount.get();
            // Đặt người dùng vào session
            session.setAttribute("currentUser", currentUser);
            
            // Kiểm tra vai trò: nếu admin (role = "ROLE_ADMIN", "admin" hoặc "1") thì chuyển hướng tới /admin
            if ("ROLE_ADMIN".equalsIgnoreCase(currentUser.getRole()) ||
                "admin".equalsIgnoreCase(currentUser.getRole()) ||
                "1".equals(currentUser.getRole())) {
                return "redirect:/admin";
            } else {
                return "redirect:/home/index";
            }
        } else {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng!");
            return "auth/login";
        }
    }
}
