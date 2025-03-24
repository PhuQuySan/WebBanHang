package com.asm;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderUtil {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "adminpass"; // Mật khẩu gốc
        String encodedPassword = encoder.encode(rawPassword); // Mã hóa mật khẩu
        System.out.println("Mật khẩu mã hóa: " + encodedPassword);
    }
}