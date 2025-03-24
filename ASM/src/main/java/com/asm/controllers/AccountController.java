package com.asm.controllers;

import com.asm.models.Account;
import com.asm.services.AccountService;
import com.asm.services.EmailService;
import com.asm.repositories.AccountRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("account", new Account());
        return "account/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("account") Account account,
                           @RequestParam(required = false) String confirmPassword,
                           Model model) {
        // 1. Kiểm tra email trước tiên
        if (account.getEmail() == null || !isValidEmail(account.getEmail())) {
            model.addAttribute("error", "Email không hợp lệ!");
            return "account/register";
        }

        // 2. Kiểm tra số điện thoại
        if (account.getPhone() == null || !account.getPhone().matches("^\\d{10}$")) {
            model.addAttribute("error", "Số điện thoại không hợp lệ!");
            return "account/register";
        }

        // 3. Kiểm tra username
        if (account.getUsername() == null || account.getUsername().trim().isEmpty()) {
            model.addAttribute("error", "Vui lòng nhập tên đăng nhập!");
            return "account/register";
        }

        String username = account.getUsername().trim();
        if (username.length() < 3 || username.length() > 50) {
            model.addAttribute("error", "Tên đăng nhập phải có từ 3-50 ký tự!");
            return "account/register";
        }

        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            model.addAttribute("error", "Tên đăng nhập chỉ được chứa chữ cái, số và dấu gạch dưới!");
            return "account/register";
        }

        // 4. Kiểm tra username đã tồn tại
        if (accountService.findByUsername(account.getUsername()).isPresent()) {
            model.addAttribute("error", "Tên đăng nhập đã tồn tại!");
            return "account/register";
        }

        // 5. Kiểm tra email đã tồn tại
        if (accountService.findByEmail(account.getEmail()).isPresent()) {
            model.addAttribute("error", "Email đã được sử dụng!");
            return "account/register";
        }

        // 6. Kiểm tra mật khẩu
        if (account.getPassword() == null || account.getPassword().trim().isEmpty()) {
            model.addAttribute("error", "Vui lòng nhập mật khẩu!");
            return "account/register";
        }

        String password = account.getPassword();
        if (password.length() < 6) {
            model.addAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự!");
            return "account/register";
        }

        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")) {
            model.addAttribute("error", "Mật khẩu phải chứa ít nhất 1 chữ hoa, 1 chữ thường và 1 số!");
            return "account/register";
        }

        // 7. Kiểm tra mật khẩu xác nhận
        if (confirmPassword == null || !account.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp với mật khẩu đã nhập!");
            return "account/register";
        }

        // 8. Đăng ký tài khoản mới
        Account savedAccount;
        try {
            savedAccount = accountService.register(account);
        } catch (Exception e) {
            model.addAttribute("error", "Đăng ký không thành công. Vui lòng thử lại sau.");
            return "account/register";
        }

        // 9. Gửi email kích hoạt
        try {
            String activationLink = "http://localhost:8080/account/activate?token=" + savedAccount.getActivationToken();
            emailService.sendActivationEmail(savedAccount.getEmail(), activationLink);
            model.addAttribute("message", "Đăng ký thành công! Vui lòng kiểm tra email để kích hoạt tài khoản.");
            return "account/register-success";
        } catch (Exception e) {
            model.addAttribute("message", "Đăng ký thành công! Tuy nhiên, gửi email kích hoạt thất bại, vui lòng liên hệ hỗ trợ.");
            return "account/register-success";
        }
    }

    private boolean isValidEmail(String email) {
        // Email validation regex
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        if (email == null) return false;

        // Check basic format
        if (!email.matches(emailRegex)) return false;

        // Additional validation
        String[] parts = email.split("@");
        if (parts.length != 2) return false;

        // Check for consecutive dots
        if (parts[1].contains("..")) return false;

        // Check for spaces
        if (email.contains(" ")) return false;

        // Check for non-ASCII characters
        if (!email.matches("\\A\\p{ASCII}*\\z")) return false;

        return true;
    }

    @GetMapping("/activate")
    public String activateAccount(@RequestParam("token") String token, Model model) {
        if (accountService.activateAccount(token).isPresent()) {
            model.addAttribute("message", "Tài khoản đã được kích hoạt thành công!");
        } else {
            model.addAttribute("error", "Liên kết kích hoạt không hợp lệ!");
        }
        return "account/activation-result";
    }
    // Quên mk
    @GetMapping("/forgot-password")
    public String showforgotPassword() {
        return "account/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String username,
            Model model) {

        // Tìm tài khoản theo email hoặc username
        Optional<Account> accountOptional = accountService.findByEmailOrUsername(email, username);

        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();

            // Tạo reset token mới và gán vào tài khoản
            String resetToken = UUID.randomUUID().toString();
            account.setResetToken(resetToken);

            // Lưu lại tài khoản
            accountRepository.save(account);

            // Xây dựng liên kết reset password
            String resetLink = "http://localhost:8080/account/reset-password?token=" + resetToken;

            // Gửi email reset password
            emailService.sendForgotPasswordEmail(account.getEmail(), account.getUsername(), resetLink);

            model.addAttribute("message"," Chúng tôi đã gửi một liên kết đặt lại mật khẩu vào email của bạn.");
            model.addAttribute("resetLink", resetLink);
        } else {
            model.addAttribute("error", "Không tìm thấy tài khoản với thông tin đã nhập.");
        }
        return "account/forgot-password";
    }

    // ResetPass
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        Optional<Account> accountOptional = accountService.findByResetToken(token);

        if (accountOptional.isPresent()) {
            model.addAttribute("token", token);
            return "account/reset-password";
        } else {
            model.addAttribute("error", "Liên kết đặt lại mật khẩu không hợp lệ hoặc đã hết hạn.");
            return "account/forgot-password";
        }
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu mới và xác nhận mật khẩu không khớp.");
            model.addAttribute("token", token);
            return "account/reset-password";
        }

        Optional<Account> accountOptional = accountService.findByResetToken(token);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            account.setPassword(passwordEncoder.encode(newPassword));
            account.setResetToken(null);
            accountService.save(account);

            model.addAttribute("message", "Đặt lại mật khẩu thành công! Bạn có thể đăng nhập.");
            return "redirect:/auth/login";
        } else {
            model.addAttribute("error", "Liên kết đặt lại mật khẩu không hợp lệ hoặc đã hết hạn.");
            return "account/reset-password";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/home/index";
    }

    @GetMapping("/update")
    public String showUpdateForm(HttpSession session, Model model) {
        Account currentUser = (Account) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("currentUser", currentUser);
        return "account/update";
    }

    @PostMapping("/update")
    public String updateAccount(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam("avatar") MultipartFile avatarFile,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Account currentUser = (Account) session.getAttribute("currentUser");
        if (currentUser == null) {
            System.out.println("User chưa đăng nhập.");
            return "redirect:/auth/login";
        }

        System.out.println("=== BẮT ĐẦU CẬP NHẬT THÔNG TIN ===");
        System.out.println("Tên đăng nhập: " + username);
        System.out.println("Email: " + email);
        System.out.println("Password: " + password);

        currentUser.setUsername(username);
        currentUser.setEmail(email);
        currentUser.setPassword(passwordEncoder.encode(password));

        if (!avatarFile.isEmpty()) {
            System.out.println("File ảnh nhận được: " + avatarFile.getOriginalFilename());
            try {
                String currentDir = System.getProperty("user.dir");
                String uploadDirPath = currentDir + "/Uploads";

                File uploadDir = new File(uploadDirPath);
                if (!uploadDir.exists()) {
                    boolean isDirCreated = uploadDir.mkdirs();
                    System.out.println("Tạo thư mục Uploads: " + (isDirCreated ? "Thành công" : "Thất bại"));
                }

                String filePath = uploadDirPath + "/" + avatarFile.getOriginalFilename();
                avatarFile.transferTo(new File(filePath));
                System.out.println("Ảnh đã được lưu tại: " + filePath);

                currentUser.setAvatar("/Uploads/" + avatarFile.getOriginalFilename());
                System.out.println("Đường dẫn ảnh trong CSDL: " + currentUser.getAvatar());
            } catch (IOException e) {
                System.out.println("Lỗi khi upload ảnh: " + e.getMessage());
                redirectAttributes.addFlashAttribute("error", "Không thể upload ảnh đại diện.");
                return "redirect:/account/update";
            }
        } else {
            if (currentUser.getAvatar() == null || currentUser.getAvatar().isEmpty()) {
                currentUser.setAvatar("/Uploads/default-avatar.png");
            }
            System.out.println("Không có file ảnh nào được upload.");
        }

        try {
            accountService.save(currentUser);
            session.setAttribute("currentUser", currentUser);
            System.out.println("Thông tin người dùng đã được lưu vào CSDL.");
        } catch (Exception e) {
            System.out.println("Lỗi khi lưu thông tin vào CSDL: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Không thể lưu thông tin tài khoản.");
            return "redirect:/account/update";
        }

        redirectAttributes.addFlashAttribute("message", "Cập nhật thông tin thành công!");
        System.out.println("=== HOÀN THÀNH CẬP NHẬT ===");
        return "redirect:/home/index";
    }
}

//package com.asm.controllers;
//
//import com.asm.models.Account;
//
//import com.asm.services.AccountService;
//import com.asm.services.EmailService;
//
//import jakarta.servlet.http.HttpSession;
//import jakarta.validation.Valid;
//
//import java.io.File;
//import java.io.IOException;
//
//import java.util.Optional;
//import java.util.UUID;
//
//import org.springframework.beans.factory.annotation.Autowired;
//
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import com.asm.repositories.AccountRepository;
//
//
//@Controller
//@RequestMapping("/account")
//public class AccountController {
//
//    @Autowired
//    private AccountService accountService;
//
//    @Autowired
//    private EmailService emailService;
//
//    @Autowired
//    private AccountRepository accountRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//
//    // Hiển thị form đăng ký tài khoản
//    @GetMapping("/register")
//    public String showRegisterForm(Model model) {
//        model.addAttribute("account", new Account());
//        return "account/register";
//    }
//
//    @PostMapping("/register")
//    public String register(@ModelAttribute("account") Account account, Model model) {
//        if (accountService.findByUsername(account.getUsername()).isPresent()) {
//            model.addAttribute("error", "Tên đăng nhập đã tồn tại!");
//            return "account/register";
//        }
//        Account savedAccount = accountService.register(account);
//        String activationLink = "http://localhost:8080/account/activate?token=" + savedAccount.getActivationToken();
//        emailService.sendActivationEmail(savedAccount.getEmail(), activationLink);
//
//        model.addAttribute("message", "Đăng ký thành công! Vui lòng kiểm tra email để kích hoạt tài khoản.");
//        return "account/register-success";
//    }
//
//
//
//  //Active tài khoản
//    @GetMapping("/activate")
//    public String activateAccount(@RequestParam("token") String token, Model model) {
//        if (accountService.activateAccount(token).isPresent()) {
//            model.addAttribute("message", "Tài khoản đã được kích hoạt thành công!");
//        } else {
//            model.addAttribute("error", "Liên kết kích hoạt không hợp lệ!");
//        }
//        return "account/activation-result";
//    }
//
//
//    // Quen mk
//
//    @GetMapping("/forgot-password")
//    public String showforgotPassword() {
//        return "account/forgot-password";
//    }
//
//    @PostMapping("/forgot-password")
//    public String forgotPassword(
//            @RequestParam(required = false) String email,
//            @RequestParam(required = false) String username,
//            Model model) {
//
//        // Tìm tài khoản theo email hoặc username
//        Optional<Account> accountOptional = accountService.findByEmailOrUsername(email, username);
//
//        if (accountOptional.isPresent()) {
//            Account account = accountOptional.get();
//
//            // Tạo reset token mới và gán vào tài khoản
//            String resetToken = UUID.randomUUID().toString();
//            account.setResetToken(resetToken);
//
//            // Lưu lại tài khoản (bạn có thể gọi accountService.save(account) nếu có)
//            accountRepository.save(account);
//
//            // Xây dựng liên kết reset password
//            String resetLink = "http://localhost:8080/account/reset-password?token=" + resetToken;
//
//            // Gửi email reset password (đảm bảo emailService đã được cấu hình đúng)
//            emailService.sendForgotPasswordEmail(account.getEmail(), account.getUsername(), resetLink);
//
//
//            // Thêm thông báo thành công vào model, hiển thị username và liên kết reset
//            model.addAttribute("message"," Chúng tôi đã gửi một liên kết đặt lại mật khẩu vào email của bạn.");
//            model.addAttribute("resetLink", resetLink);
//        } else {
//            model.addAttribute("error", "Không tìm thấy tài khoản với thông tin đã nhập.");
//        }
//        return "account/forgot-password";
//    }
//
//// ResetPass
//    @GetMapping("/reset-password")
//    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
//        Optional<Account> accountOptional = accountService.findByResetToken(token);
//
//        if (accountOptional.isPresent()) {
//            model.addAttribute("token", token); // Gửi token vào form HTML
//            return "account/reset-password";
//        } else {
//            model.addAttribute("error", "Liên kết đặt lại mật khẩu không hợp lệ hoặc đã hết hạn.");
//            return "account/forgot-password";
//        }
//    }
//
//    @PostMapping("/reset-password")
//    public String resetPassword(
//            @RequestParam("token") String token,
//            @RequestParam("newPassword") String newPassword,
//            @RequestParam("confirmPassword") String confirmPassword,
//            Model model) {
//
//        if (!newPassword.equals(confirmPassword)) {
//            model.addAttribute("error", "Mật khẩu mới và xác nhận mật khẩu không khớp.");
//            model.addAttribute("token", token);
//            return "account/reset-password";
//        }
//
//        Optional<Account> accountOptional = accountService.findByResetToken(token);
//        if (accountOptional.isPresent()) {
//            Account account = accountOptional.get();
//            account.setPassword(passwordEncoder.encode(newPassword)); // Mã hóa mật khẩu mới
//            account.setResetToken(null); // Xóa token sau khi sử dụng
//            accountService.save(account);
//
//            model.addAttribute("message", "Đặt lại mật khẩu thành công! Bạn có thể đăng nhập.");
//            return "redirect:/auth/login";
//        } else {
//            model.addAttribute("error", "Liên kết đặt lại mật khẩu không hợp lệ hoặc đã hết hạn.");
//            return "account/reset-password";
//        }
//    }
//
//
//    @GetMapping("/logout")
//    public String logout(HttpSession session) {
//        // Xóa thông tin người dùng trong session
//        session.invalidate();
//        return "redirect:/home/index"; // Chuyển về trang chủ sau khi đăng xuất
//    }
//
//
//
////    // Doi Mat Khau
////    @GetMapping("/change-password")
////    public String showChangePass() {
////        return "account/change-password";
////    }
////
////
//
//
//
//
////    User Interface
//
//    @GetMapping("/update")
//    public String showUpdateForm(HttpSession session, Model model) {
//        Account currentUser = (Account) session.getAttribute("currentUser");
//        if (currentUser == null) {
//            return "redirect:/auth/login"; // Chuyển đến trang đăng nhập nếu chưa đăng nhập
//        }
//        model.addAttribute("currentUser", currentUser); // Gửi thông tin người dùng đến form
//        return "account/update";
//    }
//
//    @PostMapping("/update")
//    public String updateAccount(
//            @RequestParam String username,
//            @RequestParam String email,
//            @RequestParam("avatar") MultipartFile avatarFile,
//            @RequestParam String password,
//            HttpSession session,
//            RedirectAttributes redirectAttributes) {
//
//        Account currentUser = (Account) session.getAttribute("currentUser");
//        if (currentUser == null) {
//            System.out.println("User chưa đăng nhập.");
//            return "redirect:/auth/login";
//        }
//
//        System.out.println("=== BẮT ĐẦU CẬP NHẬT THÔNG TIN ===");
//        System.out.println("Tên đăng nhập: " + username);
//        System.out.println("Email: " + email);
//        System.out.println("Password: " + password);
//
//        // Cập nhật thông tin
//        currentUser.setUsername(username);
//
//        currentUser.setEmail(email);
//        currentUser.setPassword(passwordEncoder.encode(password)); // Mã hóa mật khẩu mới
//
//        // Xử lý upload ảnh đại diện
//        if (!avatarFile.isEmpty()) {
//            System.out.println("File ảnh nhận được: " + avatarFile.getOriginalFilename());
//            try {
//                // Sử dụng thư mục ngoài dự án
//                String currentDir = System.getProperty("user.dir");
//                String uploadDirPath = currentDir + "/Uploads";
//
//                // Tạo thư mục nếu chưa tồn tại
//                File uploadDir = new File(uploadDirPath);
//                if (!uploadDir.exists()) {
//                    boolean isDirCreated = uploadDir.mkdirs();
//                    System.out.println("Tạo thư mục Uploads: " + (isDirCreated ? "Thành công" : "Thất bại"));
//                }
//
//                // Đường dẫn file
//                String filePath = uploadDirPath + "/" + avatarFile.getOriginalFilename();
//                avatarFile.transferTo(new File(filePath));
//                System.out.println("Ảnh đã được lưu tại: " + filePath);
//
//                // Cập nhật đường dẫn URL (cho người dùng truy cập)
//                currentUser.setAvatar("/Uploads/" + avatarFile.getOriginalFilename());
//                System.out.println("Đường dẫn ảnh trong CSDL: " + currentUser.getAvatar());
//            } catch (IOException e) {
//                System.out.println("Lỗi khi upload ảnh: " + e.getMessage());
//                redirectAttributes.addFlashAttribute("error", "Không thể upload ảnh đại diện.");
//                return "redirect:/account/update";
//            }
//        } else {
//            // Sử dụng avatar mặc định nếu người dùng không upload ảnh
//            if (currentUser.getAvatar() == null || currentUser.getAvatar().isEmpty()) {
//                currentUser.setAvatar("/Uploads/default-avatar.png");
//            }
//            System.out.println("Không có file ảnh nào được upload.");
//        }
//
//        // Lưu thông tin vào CSDL
//        try {
//            accountService.save(currentUser);
//            session.setAttribute("currentUser", currentUser); // Cập nhật thông tin vào session
//            System.out.println("Thông tin người dùng đã được lưu vào CSDL.");
//        } catch (Exception e) {
//            System.out.println("Lỗi khi lưu thông tin vào CSDL: " + e.getMessage());
//            redirectAttributes.addFlashAttribute("error", "Không thể lưu thông tin tài khoản.");
//            return "redirect:/account/update";
//        }
//
//        redirectAttributes.addFlashAttribute("message", "Cập nhật thông tin thành công!");
//        System.out.println("=== HOÀN THÀNH CẬP NHẬT ===");
//        return "redirect:/home/index";
//    }
//
//
//}