package com.asm.services;


import com.asm.models.Account;
import com.asm.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;
    

    @Autowired
    private PasswordEncoder passwordEncoder; // Mã hóa mật khẩu

    // Đăng ký tài khoản mới và tạo token kích hoạt
    public Account register(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword())); // Mã hóa mật khẩu
        account.setRole("ROLE_USER"); // Mặc định là người dùng
        account.setActivated(false); // Tài khoản chưa được kích hoạt
        account.setActivationToken(UUID.randomUUID().toString()); // Tạo token kích hoạt
        return accountRepository.save(account);
    }

 // Xử lý đăng nhập
    public boolean login(String username, String password) {
        Optional<Account> optionalAccount = accountRepository.findByUsername(username);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            // So sánh mật khẩu đã mã hóa
            boolean matches = passwordEncoder.matches(password, account.getPassword());
            if (matches) {
                System.out.println("Đăng nhập thành công cho tài khoản: " + username);
                return true;
            } else {
                System.out.println("Sai mật khẩu cho tài khoản: " + username);
            }
        } else {
            System.out.println("Tài khoản không tồn tại: " + username);
        }
        return false;
    }

    // Kích hoạt tài khoản bằng token
    public Optional<Account> activateAccount(String token) {
        Optional<Account> accountOptional = accountRepository.findByActivationToken(token);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            account.setActivated(true); // Kích hoạt tài khoản
            account.setActivationToken(null); // Xóa token sau khi kích hoạt
            accountRepository.save(account);
            return Optional.of(account);
        }
        return Optional.empty(); // Token không hợp lệ
    }
    
    // Đổi mật khẩu
    public boolean changePassword(String username, String currentPassword, String newPassword) {
        Optional<Account> optionalAccount = accountRepository.findByUsername(username);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            if (passwordEncoder.matches(currentPassword, account.getPassword())) {
                account.setPassword(passwordEncoder.encode(newPassword));
                accountRepository.save(account);
                return true;
            }
        }
        return false;
    }
    // Save
    public Account save(Account account) {
        return accountRepository.save(account); // Lưu tài khoản vào cơ sở dữ liệu
    }
    
    //Tìm tài khoản bằng token
    public Optional<Account> findByResetToken(String token) {
        return accountRepository.findByResetToken(token);
    }

    
    // Tìm tài khoản theo tên đăng nhập
    public Optional<Account> findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    // Tìm tài khoản theo email
    public Optional<Account> findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }
 // Tìm tài khoản theo email, username
    public Optional<Account> findByEmailOrUsername(String email, String username) {
        return accountRepository.findByEmailOrUsername(email, username);
    }

    // --- Phương thức bổ sung cho Admin ---

    // Lấy tất cả tài khoản
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    // Tìm tài khoản theo ID
    public Optional<Account> findById(Integer id) {
        return accountRepository.findById(id);
    }
    

    /**
     * Phương thức thay đổi mật khẩu cho admin update tài khoản.
     *  Admin không cần kiểm tra mật khẩu cũ.
     */
    public Account changePassword(Account account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword));
        return accountRepository.save(account);
    }


    // Xóa tài khoản theo id
    public void deleteById(Integer id) {
        accountRepository.deleteById(id);
    }
}