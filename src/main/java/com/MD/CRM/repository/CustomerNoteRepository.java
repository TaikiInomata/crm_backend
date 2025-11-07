package com.MD.CRM.repository;

import com.MD.CRM.entity.CustomerNote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerNoteRepository extends JpaRepository<CustomerNote, String> {
    @Query("""
            SELECT n FROM CustomerNote n
            WHERE (:customerId IS NULL OR n.customer.id = :customerId)
              AND (:staffId IS NULL OR n.staff.id = :staffId)
            """)
    Page<CustomerNote> findByFilters(@Param("customerId") String customerId,
                                     @Param("staffId") String staffId,
                                     Pageable pageable);

}
