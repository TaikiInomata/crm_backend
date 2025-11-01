package com.MD.CRM.repository;

import com.MD.CRM.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    /**
     * Check if a customer with the given email exists (excluding soft-deleted records)
     * @param email the email to check
     * @return true if exists, false otherwise
     */
    boolean existsByEmailAndDeletedAtIsNull(String email);

    /**
     * Check if a customer with the given phone number exists (excluding soft-deleted records)
     * @param phone the phone number to check
     * @return true if exists, false otherwise
     */
    boolean existsByPhoneAndDeletedAtIsNull(String phone);

    /**
     * Find a customer by email (excluding soft-deleted records)
     * @param email the email to search
     * @return Optional containing the customer if found
     */
    Optional<Customer> findByEmailAndDeletedAtIsNull(String email);

    /**
     * Find a customer by phone number (excluding soft-deleted records)
     * @param phone the phone number to search
     * @return Optional containing the customer if found
     */
    Optional<Customer> findByPhoneAndDeletedAtIsNull(String phone);
}
