package com.MD.CRM.repository;

import com.MD.CRM.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, String> {

    @Query("SELECT a FROM ActivityLog a " +
           "WHERE (:userId IS NULL OR a.user.id = :userId) " +
           "AND (:action IS NULL OR a.action = :action) " +
           "AND (:from IS NULL OR a.createdAt >= :from) " +
           "AND (:to IS NULL OR a.createdAt <= :to)")
    Page<ActivityLog> findByFilters(@Param("userId") String userId,
                                    @Param("action") String action,
                                    @Param("from") LocalDateTime from,
                                    @Param("to") LocalDateTime to,
                                    Pageable pageable);
}
