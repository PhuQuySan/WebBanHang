package com.asm.repositories;

import com.asm.models.Account;
import com.asm.models.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByAccount(Account account);
}


//package com.asm.repositories;
//
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import com.asm.models.Orders;
//
//@Repository
//public interface OrderRepository extends JpaRepository<Orders, Long> {
//    // Các phương thức truy vấn bổ sung nếu cần
//}
//
//
////import org.springframework.data.jpa.repository.JpaRepository;
////
////import com.asm.models.Orders;
////
////public interface OrderRepository extends JpaRepository<Orders, Integer> {
////	
////}
