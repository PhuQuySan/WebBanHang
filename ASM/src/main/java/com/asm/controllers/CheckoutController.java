package com.asm.controllers;

import com.asm.models.*;
import com.asm.services.OrderService;
import com.asm.services.OrderUtil;
import com.asm.services.ShoppingCart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller

public class CheckoutController {
    
    @Autowired
    private ShoppingCart cart;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private ShoppingCart shoppingCart;   
	
	
    /**
     * Hiển thị trang Checkout.
     */

    
    @GetMapping("checkout")
    public String showCheckout(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // Kiểm tra đăng nhập
        Account currentUser = (Account) session.getAttribute("currentUser");
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập rồi mua hàng sau.");
            return "redirect:/auth/login";
        }
        

        
        // Lấy giỏ hàng từ session (ShoppingCart đã được cấu hình với @SessionScope)

        if (shoppingCart == null || shoppingCart.getItems().length == 0) {
            redirectAttributes.addFlashAttribute("error", "Giỏ hàng trống.");
            return "redirect:/cart/view";
        }
        model.addAttribute("cart", shoppingCart);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("order", new Orders());
        return "cart/checkout"; // View checkout.html
    }
    /**
     * Xử lý đặt hàng từ trang Checkout.
     */
    
    
    @PostMapping("/checkout/process")
    public String processCheckout(@ModelAttribute("order") Orders order,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        // Kiểm tra người dùng đã đăng nhập hay chưa
        Account currentUser = (Account) session.getAttribute("currentUser");
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập rồi mua hàng sau.");
            return "redirect:/auth/login";
        }

        // Kiểm tra giỏ hàng có sản phẩm hay không
        if (cart.getItems().length == 0) {
            redirectAttributes.addFlashAttribute("error", "Giỏ hàng trống.");
            return "redirect:/cart/view";
        }

        // Validate các trường bắt buộc, ví dụ: shippingAddress (địa chỉ giao hàng)
        if (order.getShippingAddress() == null || order.getShippingAddress().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Địa chỉ giao hàng không được để trống.");
            return "redirect:/cart/checkout";
        }
        
        // Chuyển đổi các sản phẩm trong giỏ hàng thành danh sách OrderDetail
        List<OrderDetail> orderDetails = OrderUtil.convertCartToOrderDetails(cart);

        // Gán thông tin người dùng cho đơn hàng
        order.setAccount(currentUser);

        // Tạo đơn hàng bằng cách gọi service
        Orders savedOrder = orderService.createOrder(order, orderDetails);

        // Sau khi đặt hàng thành công, xóa giỏ hàng
        cart.clear();

        // Đưa đơn hàng vừa tạo vào flash attribute để chuyển sang trang xác nhận
        redirectAttributes.addFlashAttribute("order", savedOrder);

        // Chuyển hướng đến trang Order Received
        return "redirect:/checkout/order-received";
    }

    /**
     * Hiển thị trang xác nhận đơn hàng (Order Received).
     * Đường dẫn: /checkout/order-received
     */
    @GetMapping("/checkout/order-received")
    public String orderReceived(Model model, RedirectAttributes redirectAttributes) {
        // Flash attribute "order" được truyền từ quá trình checkout
        if (!model.containsAttribute("order")) {
            // Nếu không có flash attribute, chuyển hướng (ví dụ: về trang chủ)
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin đơn hàng. Vui lòng thử lại.");
            return "redirect:/";
        }
        // View "checkout/order-received" sẽ hiển thị thông tin đơn hàng từ model attribute "order"
        return "checkout/order-received";
    }
    
    
    @GetMapping("/checkout/order-received/{id}")
    public String orderReceived(@PathVariable Long id, Model model) {
        Orders order = orderService.findById(id);
        if (order == null) {
            return "redirect:/error"; // Redirect nếu không tìm thấy đơn hàng
        }
        model.addAttribute("order", order);
        return "checkout/order-received";
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
//    @PostMapping("checkout/process")
//    public String processCheckout(@ModelAttribute("order") Orders order,
//                                  HttpSession session,
//                                  RedirectAttributes redirectAttributes) {
//        // Kiểm tra xem người dùng đã đăng nhập chưa
//        Account currentUser = (Account) session.getAttribute("currentUser");
//        if (currentUser == null) {
//            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập rồi mua hàng sau.");
//            return "redirect:/auth/login";
//        }
//        
//        // Kiểm tra xem giỏ hàng có sản phẩm không
//        if (cart.getItems().length == 0) {
//            redirectAttributes.addFlashAttribute("error", "Giỏ hàng trống.");
//            return "redirect:/cart/view";
//        }
//        
//        // Chuyển đổi các sản phẩm trong giỏ hàng thành danh sách OrderDetail
//        List<OrderDetail> orderDetails = OrderUtil.convertCartToOrderDetails(cart);
//        
//        // Gán thông tin người dùng cho đơn hàng
//        order.setAccount(currentUser);
//        
//        // Lưu đơn hàng, trong đó OrderService sẽ tính toán tổng tiền và thiết lập quan hệ giữa Orders và OrderDetail
//        Orders savedOrder = orderService.createOrder(order, orderDetails);
//        
//        // Xóa giỏ hàng sau khi đặt hàng thành công
//        cart.clear();
//        
//        // Xử lý thông báo dựa trên phương thức thanh toán được chọn
//        String paymentMethod = order.getPaymentMethod();  // Giá trị từ form: ví dụ "bank" hoặc "cod"
//        if (paymentMethod != null) {
//            if (paymentMethod.equalsIgnoreCase("bank")) {
//                // Thanh toán chuyển khoản ngân hàng trực tiếp
//                redirectAttributes.addFlashAttribute("message", 
//                    "Đơn hàng của bạn đã được đặt thành công với mã đơn: " + savedOrder.getId() +
//                    ". Vui lòng chuyển khoản ngân hàng trực tiếp để thanh toán.");
//            } else if (paymentMethod.equalsIgnoreCase("cod")) {
//                // Thanh toán khi nhận hàng
//                redirectAttributes.addFlashAttribute("message", 
//                    "Đơn hàng của bạn đã được đặt thành công với mã đơn: " + savedOrder.getId() +
//                    ". Bạn sẽ thanh toán khi nhận hàng.");
//            } else {
//                redirectAttributes.addFlashAttribute("message", 
//                    "Đơn hàng của bạn đã được đặt thành công với mã đơn: " + savedOrder.getId());
//            }
//        } else {
//            redirectAttributes.addFlashAttribute("message", 
//                    "Đơn hàng của bạn đã được đặt thành công với mã đơn: " + savedOrder.getId());
//        }
//        
//        return "redirect:/cart/view";
//    }   

}