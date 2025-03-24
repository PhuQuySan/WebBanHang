package com.asm.services;


import java.math.BigDecimal;
import java.util.List;
import com.asm.models.Orders;
import com.asm.models.Account;
import com.asm.models.OrderDetail;
import com.asm.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    /**
     * Tạo đơn hàng mới và tính toán tổng tiền dựa trên danh sách OrderDetail.
     */
//    public Orders createOrder(Orders order, List<OrderDetail> orderDetails) {
//        BigDecimal total = orderDetails.stream()
//            .map(od -> od.getUnitPrice().multiply(new BigDecimal(od.getQuantity())))
//            .reduce(BigDecimal.ZERO, BigDecimal::add);
//        order.setTotalAmount(total);
//        
//        // Liên kết các OrderDetail với Order
//        orderDetails.forEach(od -> od.setOrder(order));
//        orderDetails.forEach(detail -> detail.setOrder(order));
//        
//        return orderRepository.save(order);
//    }
    public Orders createOrder(Orders order, List<OrderDetail> orderDetails) {
        // Tính tổng tiền từ OrderDetail
        BigDecimal total = BigDecimal.ZERO;
        for (OrderDetail detail : orderDetails) {
            // Gán đơn hàng cho mỗi OrderDetail
            detail.setOrder(order);
            total = total.add(detail.getUnitPrice().multiply(new BigDecimal(detail.getQuantity())));
        }
        order.setTotalAmount(total);
        // Gán danh sách OrderDetail cho Order
        order.setOrderDetails(orderDetails);
        
        
        // Lưu đơn hàng; nếu cascade = ALL đã được thiết lập, các OrderDetail cũng sẽ được persist
        return orderRepository.save(order);
    }   
    public Orders findById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }
    
    // Lấy lịch sử đơn hàng của người dùng
    public List<Orders> getOrdersByAccount(Account account) {
        return orderRepository.findByAccount(account);
    }
    
}




//import java.math.BigDecimal;
//
//import com.asm.models.Orders;
//
//import jakarta.persistence.*;
//
//@Entity
//@Table(name = "OrderDetail")
//public class OrderService {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;
//    
//    // Liên kết tới Order
//    @ManyToOne
//    @JoinColumn(name = "order_id")
//    private Orders order;
//    
//    // Lưu product id (hoặc có thể liên kết tới entity Product nếu có)
//    @Column(name = "product_id")
//    private Integer productId;
//    
//    @Column(name = "quantity", nullable = false)
//    private Integer quantity;
//    
//    @Column(name = "unit_price", nullable = false)
//    private BigDecimal unitPrice;
//
//    // Getters & Setters
//
//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
//
//    public Order getOrder() {
//        return order;
//    }
//
//    public void setOrder(Order order) {
//        this.order = order;
//    }
//
//    public Integer getProductId() {
//        return productId;
//    }
//
//    public void setProductId(Integer productId) {
//        this.productId = productId;
//    }
//
//    public Integer getQuantity() {
//        return quantity;
//    }
//
//    public void setQuantity(Integer quantity) {
//        this.quantity = quantity;
//    }
//
//    public BigDecimal getUnitPrice() {
//        return unitPrice;
//    }
//
//    public void setUnitPrice(BigDecimal unitPrice) {
//        this.unitPrice = unitPrice;
//    }
//}


//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.asm.models.Account;
//import com.asm.models.Orders;
//import com.asm.repositories.OrderDetailRepository;
//import com.asm.repositories.OrderRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class OrderService {
//
//    @Autowired
//    private OrderRepository orderRepository;
//
//    @Autowired
//    private OrderDetailRepository orderDetailRepository;
//
//    public Orders saveOrder(Orders order) {
//        return orderRepository.save(order);
//    }
//
//    public List<Orders> findOrdersByAccount(Account account) {
//        return orderRepository.findAll().stream()
//                .filter(o -> o.getAccount().equals(account))
//                .toList();
//    }
//
//    public Optional<Orders> findOrderById(Integer id) {
//        return orderRepository.findById(id);
//    }
//}
