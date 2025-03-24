package com.asm.models;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Orders")
public class Orders {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    // Liên kết đến Account (người dùng)
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    
    @Column(name = "order_date")
    private LocalDateTime orderDate = LocalDateTime.now();
    
    @Column(name = "total_amount")
    private BigDecimal totalAmount;
    
    @Column(name = "status")
    private String status = "PENDING";
    
    // Thông tin thanh toán/giao hàng
    @Column(name = "country", nullable = false)
    private String country = "Việt Nam";
    
    @Column(name = "shipping_address", nullable = false)
    private String shippingAddress;
    
    @Column(name = "phone", nullable = false)
    private String phone;
    
    @Column(name = "email", nullable = false)
    private String email;
    
    @Column(name = "order_note")
    private String orderNote;
    
    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;
    
    // Quan hệ 1-N với OrderDetail, khởi tạo danh sách mặc định
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    //    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
//    @Builder.Default
//    private List<OrderDetail> orderDetails = new ArrayList<>();
}