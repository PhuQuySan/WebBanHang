package com.asm.repositories;

import com.asm.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByUsername(String username);

    Optional<Account> findByActivationToken(String token); // Tìm tài khoản bằng token
    
    Optional<Account> findByEmail(String email);

    Optional<Account> findByResetToken(String token);
    
    Optional<Account> findByEmailOrUsername(String email, String username);// Tìm email và username bằng token
}
