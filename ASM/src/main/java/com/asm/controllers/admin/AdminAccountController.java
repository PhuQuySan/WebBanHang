package com.asm.controllers.admin;

import com.asm.models.Account;
import com.asm.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/admin/accounts")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminAccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping
    public String showAccountList(Model model) {
        model.addAttribute("account", new Account());
        model.addAttribute("accounts", accountService.findAll());
        return "admin/account/accounts";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        model.addAttribute("account", new Account());
        return "redirect:/admin/accounts";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Optional<Account> accountOpt = accountService.findById(id);
        if (accountOpt.isPresent()) {
            model.addAttribute("account", accountOpt.get());
            model.addAttribute("accounts", accountService.findAll());
            return "admin/account/accounts";
        }
        ra.addFlashAttribute("error", "Tài khoản không tồn tại!");
        return "redirect:/admin/accounts";
    }

    @PostMapping("/save")
    public String saveAccount(@ModelAttribute Account account,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                              RedirectAttributes ra) {
        try {
            // Nếu có file ảnh được tải lên
            if (imageFile != null && !imageFile.isEmpty()) {
                System.out.println("File ảnh nhận được: " + imageFile.getOriginalFilename());

                // Lấy đường dẫn thư mục gốc của project
                String currentDir = System.getProperty("user.dir");
                String uploadDirPath = currentDir + "/Uploads/Accounts";

                // Tạo thư mục nếu chưa tồn tại
                File uploadDir = new File(uploadDirPath);
                if (!uploadDir.exists() && uploadDir.mkdirs()) {
                    System.out.println("Tạo thư mục Uploads/Accounts: Thành công");
                }

                // Tạo tên file duy nhất để tránh bị ghi đè
                String uniqueFileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                String filePath = uploadDirPath + "/" + uniqueFileName;

                // Lưu file vào thư mục
                imageFile.transferTo(new File(filePath));
                System.out.println("Ảnh đã được lưu tại: " + filePath);

                // Cập nhật đường dẫn ảnh vào Account
                account.setAvatar("/Uploads/Accounts/" + uniqueFileName);
            } else {
                System.out.println("Không có file ảnh nào được upload.");
                if (account.getAvatar() == null || account.getAvatar().isEmpty()) {
                    account.setAvatar("/Uploads/default-avatar.png"); // Ảnh mặc định
                }
            }

            // Nếu là tài khoản mới
            if (account.getId() == null) {
                accountService.register(account);
            } else {
                // Nếu đang cập nhật
                Optional<Account> existingAccount = accountService.findById(account.getId());
                if (existingAccount.isPresent()) {
                    Account existing = existingAccount.get();
                    // Nếu không cập nhật mật khẩu thì giữ nguyên mật khẩu cũ
                    if (account.getPassword() == null || account.getPassword().isEmpty()) {
                        account.setPassword(existing.getPassword());
                    } else {
                        // Sử dụng phương thức changePassword cho admin (không cần kiểm tra mật khẩu cũ)
                        Account updated = accountService.changePassword(existing, account.getPassword());
                        account.setPassword(updated.getPassword());
                    }
                    // Nếu avatar mới không được nhập, giữ nguyên avatar cũ
                    if (account.getAvatar() == null || account.getAvatar().trim().isEmpty()) {
                        account.setAvatar(existing.getAvatar());
                    }
                }
                accountService.save(account);
            }
            ra.addFlashAttribute("success", "Lưu tài khoản thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi khi lưu tài khoản: " + e.getMessage());
        }
        return "redirect:/admin/accounts";
    }

    @GetMapping("/delete/{id}")
    public String deleteAccount(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            accountService.deleteById(id);
            ra.addFlashAttribute("success", "Xóa tài khoản thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi khi xóa tài khoản: " + e.getMessage());
        }
        return "redirect:/admin/accounts";
    }
}
