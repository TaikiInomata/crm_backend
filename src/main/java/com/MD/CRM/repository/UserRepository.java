package com.MD.CRM.repository;

import com.MD.CRM.entity.Customer;
import com.MD.CRM.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    // ✅ Thêm method mới — tìm user theo email và is_active = true
    Optional<User> findByEmailAndIsActiveTrue(String email);

    Optional<User> findByIdAndIsActiveTrue(String id);
}
