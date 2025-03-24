package com.asm.controllers;

import com.asm.models.Account;
import com.asm.models.Cart;
import com.asm.models.OrderDetail;
import com.asm.models.Orders;
import com.asm.repositories.ProductRepository;
import com.asm.services.OrderUtil;
import com.asm.services.ShoppingCart;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import com.asm.models.*;
import com.asm.services.OrderService;



@Controller
@ControllerAdvice
public class ShoppingCartController {

	// Gio Hang
	
    @Autowired
    private ShoppingCart cart;

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private ShoppingCart shoppingCart;    
    

    // Phương thức chia sẻ dữ liệu giỏ hàng trên mọi trang
    @ModelAttribute("cart")
    public ShoppingCart getCart() {
        return cart;
    }

    @GetMapping("/cart/view")
    public String viewCart(Model model) {
        return "cart/index"; // Trả về view của giỏ hàng
    }

    @GetMapping("/cart/add/{id}")
    public String addToCart(@PathVariable int id, RedirectAttributes redirectAttributes) {
        productRepository.findById(id).ifPresentOrElse(product -> {
            // Tạo đối tượng Cart dựa trên sản phẩm và thêm vào giỏ hàng
            Cart item = new Cart(product.getId(), product.getName(), product.getImage(), product.getPrice(), 1);
            cart.add(item);
            redirectAttributes.addFlashAttribute("message", "Đã thêm sản phẩm vào giỏ hàng!");
        }, () -> {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm có id: " + id);
        });
        return "redirect:/home/index";
    }


    @GetMapping("/cart/remove/{id}")
    public String removeFromCart(@PathVariable int id, Model model) {
        cart.remove(id);
        model.addAttribute("message", "Xóa sản phẩm thành công!");
        return "redirect:/cart/view";
    }

    @GetMapping("/cart/clear")
    public String clearCart(Model model) {
        cart.clear();
        model.addAttribute("message", "Đã xóa toàn bộ giỏ hàng!");
        return "redirect:/cart/view";
    }

    @PostMapping("/cart/update/{id}")
    public String updateCart(@PathVariable int id, @RequestParam int qty, Model model) {
        if (qty < 1) {
            qty = 1; // Đảm bảo số lượng luôn >= 1
        }
        cart.update(id, qty);
        model.addAttribute("message", "Cập nhật số lượng thành công!");
        return "redirect:/cart/view";
    }


    

//    @PostMapping("/cart/checkout/process")
//    public String processCheckout(@ModelAttribute("order") Orders order,
//                                  HttpSession session,
//                                  RedirectAttributes redirectAttributes) {
//        Account currentUser = (Account) session.getAttribute("currentUser");
//        if (currentUser == null) {
//            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập rồi mua hàng sau.");
//            return "redirect:/auth/login";
//        }
//        
//        // Dùng bean cart đã được inject
//        if (cart.getItems().length == 0) {
//            redirectAttributes.addFlashAttribute("error", "Giỏ hàng trống.");
//            return "redirect:/cart/view";
//        }
//        
//        List<OrderDetail> orderDetails = OrderUtil.convertCartToOrderDetails(cart);
//        order.setAccount(currentUser);
//        
//        Orders savedOrder = orderService.createOrder(order, orderDetails);
//        
//        // Sau khi đặt hàng thành công, xóa giỏ hàng
//        cart.clear();
//        redirectAttributes.addFlashAttribute("message", "Đơn hàng của bạn đã được đặt thành công với mã đơn: " + savedOrder.getId());
//        return "redirect:/cart/view";
//    }


    
    
}