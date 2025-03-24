package com.asm.models;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String role; // Ví dụ: ROLE_USER hoặc ROLE_ADMIN

    @Column(nullable = false)
    private boolean activated; // Tài khoản có đang hoạt động hay không?

    @Column(unique = true)
    private String activationToken; // Token kích hoạt tài khoản

    private String resetToken; // Token đặt lại mật khẩu

    // Nếu không cung cấp avatar, gán avatar mặc định
    private String avatar;

    @PrePersist
    public void setDefaultAvatar() {
        if (this.avatar == null || this.avatar.isEmpty()) {
            this.avatar = "/Uploads/default-avatar.png";
        }
    }
}
