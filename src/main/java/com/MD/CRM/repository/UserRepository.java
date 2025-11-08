package com.MD.CRM.repository;

import com.MD.CRM.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if email already exists
     */
    boolean existsByEmail(String email);

    /**
     * Check if username already exists
     */
    boolean existsByUsername(String username);

    /**
     * Find active user by email (for login)
     */
    Optional<User> findByEmailAndIsActiveTrue(String email);

    /**
     * Find active user by username (for login)
     */
    Optional<User> findByUsernameAndIsActiveTrue(String username);

    /**
     * Find user by ID and active status
     */
    Optional<User> findByIdAndIsActiveTrue(String id);

    /**
     * Find all active users
     */
    List<User> findAllByIsActiveTrue();

    /**
     * Find all users by role
     */
    List<User> findAllByRole(User.Role role);

    /**
     * Find all active users by role
     */
    List<User> findAllByRoleAndIsActiveTrue(User.Role role);

    /**
     * Find all users with pagination
     */
    Page<User> findAll(Pageable pageable);

    /**
     * Find all users by active status with pagination
     */
    Page<User> findAllByIsActive(Boolean isActive, Pageable pageable);

    /**
     * Search users by keyword (username, email, or fullname)
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.fullname) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<User> searchUsers(String keyword, Pageable pageable);
    Optional<User> findByIdAndIsActiveTrue(String id);
}

