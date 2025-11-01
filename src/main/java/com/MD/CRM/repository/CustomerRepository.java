package com.MD.CRM.repository;

import com.MD.CRM.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    /**
     * Find all customers excluding soft-deleted records
     * @return List of active customers
     */
    List<Customer> findAllByDeletedAtIsNull();

    /**
     * Search customers by keyword matching fullname, email, phone, or address
     * @param keyword search keyword
     * @param pageable pagination information
     * @return Page of matching customers
     */
    @Query("SELECT c FROM Customer c WHERE c.deletedAt IS NULL " +
           "AND (:keyword IS NULL OR " +
           "LOWER(c.fullname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "c.phone LIKE CONCAT('%', :keyword, '%') OR " +
           "LOWER(c.address) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Customer> searchCustomers(String keyword, Pageable pageable);

    /**
     * Find top 5 recent customers ordered by creation date
     * @return List of 5 most recent customers
     */
    @Query("SELECT c FROM Customer c WHERE c.deletedAt IS NULL ORDER BY c.createdAt DESC LIMIT 5")
    List<Customer> findTop5ByDeletedAtIsNullOrderByCreatedAtDesc();

    /**
     * Count all customers excluding soft-deleted records
     * @return Total number of active customers
     */
    long countByDeletedAtIsNull();

    /**
     * Find customer by ID excluding soft-deleted records
     * @param id the customer ID
     * @return Optional containing the customer if found
     */
    Optional<Customer> findByIdAndDeletedAtIsNull(String id);

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

    /**
     * Return non-deleted customers pageable
     */
    org.springframework.data.domain.Page<Customer> findAllByDeletedAtIsNull(org.springframework.data.domain.Pageable pageable);

    /**
     * Search customers by keyword (case-insensitive for name/email, phone as-is).
     * Results are ordered by a simple relevance score (lower is better).
     */
    @org.springframework.data.jpa.repository.Query(
        value = "SELECT * FROM customers c WHERE c.deleted_at IS NULL AND (lower(c.fullname) LIKE CONCAT('%',:q,'%') OR lower(c.email) LIKE CONCAT('%',:q,'%') OR c.phone LIKE CONCAT('%',:qRaw,'%')) " +
            "ORDER BY (" +
            "  (CASE WHEN lower(c.fullname) = :q THEN 0 WHEN lower(c.fullname) LIKE CONCAT(:q, '%') THEN 1 WHEN lower(c.fullname) LIKE CONCAT('%',:q,'%') THEN 4 ELSE 10 END) +" +
            "  (CASE WHEN lower(c.email) = :q THEN 0 WHEN lower(c.email) LIKE CONCAT(:q, '%') THEN 1 WHEN lower(c.email) LIKE CONCAT('%',:q,'%') THEN 3 ELSE 10 END) +" +
            "  (CASE WHEN c.phone = :qRaw THEN 0 WHEN c.phone LIKE CONCAT(:qRaw, '%') THEN 1 WHEN c.phone LIKE CONCAT('%',:qRaw,'%') THEN 2 ELSE 10 END)" +
            ")",
        countQuery = "SELECT count(*) FROM customers c WHERE c.deleted_at IS NULL AND (lower(c.fullname) LIKE CONCAT('%',:q,'%') OR lower(c.email) LIKE CONCAT('%',:q,'%') OR c.phone LIKE CONCAT('%',:qRaw,'%'))",
        nativeQuery = true)
    org.springframework.data.domain.Page<Customer> searchByKeyword(@org.springframework.data.repository.query.Param("q") String q,
                                   @org.springframework.data.repository.query.Param("qRaw") String qRaw,
                                   org.springframework.data.domain.Pageable pageable);

    /**
     * Find customer by ID (including soft-deleted ones)
     * Used for soft delete and restore operations
     * @param id the customer ID
     * @return Optional containing the customer if found
     */
    Optional<Customer> findById(String id);

    /**
     * Find soft-deleted customers that are eligible for restoration (deleted within 7 days)
     * @param id the customer ID
     * @param sevenDaysAgo the date 7 days ago
     * @return Optional containing the customer if found and eligible
     */
    @Query("SELECT c FROM Customer c WHERE c.id = :id AND c.deletedAt IS NOT NULL AND c.deletedAt >= :sevenDaysAgo")
    Optional<Customer> findDeletedCustomerWithinRestorePeriod(String id, java.time.LocalDateTime sevenDaysAgo);
}
