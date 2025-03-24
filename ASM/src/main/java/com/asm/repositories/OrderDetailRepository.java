package com.asm.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.asm.models.OrderDetail;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    // Các phương thức truy vấn bổ sung nếu cần
}




//import org.springframework.data.jpa.repository.JpaRepository;
//
//import com.asm.models.OrderDetail;
//
//public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {}
