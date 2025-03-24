//package com.asm.controllers;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import com.asm.models.Account;
//import com.asm.models.Cart;
//import com.asm.models.OrderDetail;
//import com.asm.models.Orders;
//import com.asm.models.Product;
//import com.asm.repositories.ProductRepository;
//import com.asm.services.OrderService;
//import com.asm.services.ShoppingCart;
//
//import jakarta.servlet.http.HttpSession;
//
//
//@Controller
//@RequestMapping("/orders")
//public class OrderController {
//
//    @Autowired
//    private OrderService orderService;
//
//    @Autowired
//    private ShoppingCart shoppingCart;
//
//    @Autowired
//    private ProductRepository productRepository;
//
//    @PostMapping("/checkout")
//    public String checkout(HttpSession session, RedirectAttributes redirectAttributes) {
//        Account currentUser = (Account) session.getAttribute("currentUser");
//        if (currentUser == null) {
//            return "redirect:/auth/login";
//        }
//
//        // Kiểm tra giỏ hàng trống
//        if (shoppingCart.getItems().length == 0) {
//            redirectAttributes.addFlashAttribute("error", "Giỏ hàng trống! Vui lòng thêm sản phẩm trước khi thanh toán.");
//            return "redirect:/cart/view";
//        }
//        
//        // Tạo đơn hàng từ giỏ hàng
//        Orders order = new Orders();
//        order.setAccount(currentUser);
//
//        double totalAmount = 0.0;
//
//        for (Cart item : shoppingCart.getItems()) {
//            Product product = productRepository.findById(item.getId()).orElse(null);
//            if (product == null) {
//                redirectAttributes.addFlashAttribute("error", "Sản phẩm không tồn tại: " + item.getName());
//                continue;
//            }
//
//            OrderDetail detail = new OrderDetail();
//            detail.setOrder(order);
//            detail.setProduct(product);
//            detail.setQuantity(item.getQty());
//            detail.setUnitPrice(item.getPrice());
//
//            totalAmount += item.getPrice() * item.getQty();
//            order.getOrderDetails().add(detail);
//        }
//        order.setTotalAmount(totalAmount);
//        
//      
//
//
//        // Lưu đơn hàng và xóa giỏ hàng
//        orderService.saveOrder(order);
//        shoppingCart.clear();
//
//        redirectAttributes.addFlashAttribute("message", "Đặt hàng thành công!");
//        return "redirect:/orders/list";
//    }
//    @GetMapping("/list")
//    public String confirm(Model model) {
//
//        return "orders/list";
//    }
//}
//
