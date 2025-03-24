package com.asm.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.asm.models.Cart;
import com.asm.models.OrderDetail;

public class OrderUtil {
    /**
     * Chuyển đổi ShoppingCart thành danh sách OrderDetail.
     * Nếu giỏ hàng rỗng, trả về danh sách rỗng.
     */

    
	public static List<OrderDetail> convertCartToOrderDetails(ShoppingCart shoppingCart) {
	    List<OrderDetail> orderDetails = new ArrayList<>();
	    Cart[] items = shoppingCart.getItems();
	    if (items != null && items.length > 0) {
	        for (Cart item : items) {
	            OrderDetail detail = OrderDetail.builder()
	                    .productId(item.getId())
	                    .productName(item.getName())  // Gán tên sản phẩm từ Cart
	                    .quantity(item.getQty())
	                    .unitPrice(BigDecimal.valueOf(item.getPrice()))
	                    .build();
	            orderDetails.add(detail);
	        }
	    }
	    return orderDetails;
	}
}


//public static List<OrderDetail> convertCartToOrderDetails(ShoppingCart shoppingCart) {
//List<OrderDetail> orderDetails = new ArrayList<>();
//Cart[] items = shoppingCart.getItems();
//if (items != null && items.length > 0) {
//  for (Cart item : items) {
//      OrderDetail detail = new OrderDetail();
//      detail.setProductId(item.getId());
//      detail.setQuantity(item.getQty());
//      detail.setUnitPrice(BigDecimal.valueOf(item.getPrice()));
//      orderDetails.add(detail);
//  }
//}
//return orderDetails;
//}