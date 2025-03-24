package com.asm.services;

import com.asm.models.Cart;
import org.springframework.stereotype.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

@Service
@SessionScope
public class ShoppingCart {
    private static final int MAX_SIZE = 100; // Kích thước tối đa của giỏ hàng
    private final Cart[] cart = new Cart[MAX_SIZE]; // Mảng chứa sản phẩm
    private int size = 0; // Số lượng sản phẩm hiện có trong giỏ hàng

    // Thêm sản phẩm vào giỏ hàng
    public void add(Cart item) {
        System.out.println("=== Bắt đầu thêm sản phẩm ===");
        System.out.println("Sản phẩm được thêm: " + item);
        
        // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
        for (int i = 0; i < size; i++) {
            if (cart[i].getId() == item.getId()) {
                System.out.println("Sản phẩm đã tồn tại, số lượng cũ: " + cart[i].getQty());
                cart[i].setQty(cart[i].getQty() + item.getQty());
                System.out.println("Số lượng mới: " + cart[i].getQty());
                printCartItems();
                return;
            }
        }
        // Thêm sản phẩm mới nếu chưa có và mảng chưa đầy
        if (size < MAX_SIZE) {
            cart[size++] = item;
            System.out.println("Thêm sản phẩm mới vào vị trí " + (size - 1));
        } else {
            System.out.println("Giỏ hàng đã đầy, không thêm được sản phẩm.");
        }
        printCartItems();
    }
    
    

    // Xóa sản phẩm theo ID
    public void remove(int id) {
        for (int i = 0; i < size; i++) {
            if (cart[i].getId() == id) {
                System.arraycopy(cart, i + 1, cart, i, size - i - 1);
                cart[--size] = null;
                return;
            }
        }
    }

    // Cập nhật số lượng sản phẩm
    public void update(int id, int qty) {
        for (int i = 0; i < size; i++) {
            if (cart[i].getId() == id) {
                cart[i].setQty(qty);
                return;
            }
        }
    }

    // Xóa toàn bộ giỏ hàng
    public void clear() {
        for (int i = 0; i < size; i++) {
            cart[i] = null;
        }
        size = 0;
    }

    // Lấy tổng số sản phẩm trong giỏ hàng
    public int getCount() {
        int count = 0;
        for (int i = 0; i < size; i++) {
            count += cart[i].getQty();
        }
        return count;
    }

    // Tính tổng tiền trong giỏ hàng
    public double getAmount() {
        double total = 0.0;
        for (int i = 0; i < size; i++) {
            total += cart[i].getPrice() * cart[i].getQty();
        }
        return total;
    }

    // Lấy danh sách sản phẩm trong giỏ hàng (trả về mảng các đối tượng Cart)
    public Cart[] getItems() {
        Cart[] items = new Cart[size];
        System.arraycopy(cart, 0, items, 0, size);
        return items;
    }
    
    // Hàm tiện ích để in danh sách sản phẩm trong giỏ hàng
    private void printCartItems() {
        System.out.println("=== Nội dung giỏ hàng hiện tại ===");
        for (int i = 0; i < size; i++) {
            System.out.println("Vị trí " + i + ": " + cart[i]);
        }
        System.out.println("Tổng số sản phẩm: " + size);
        System.out.println("==================================");
    }
}

