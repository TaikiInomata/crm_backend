package com.MD.CRM.repository;

import com.MD.CRM.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, String> {

    /**
     * Find all logs by type
     */
    List<ActivityLog> findAllByType(ActivityLog.LogType type);

    /**
     * Find all logs by user ID
     */
    List<ActivityLog> findAllByUserId(String userId);

    /**
     * Find all logs by customer ID
     */
    List<ActivityLog> findAllByCustomerId(String customerId);

    /**
     * Find all logs by type with pagination
     */
    Page<ActivityLog> findAllByType(ActivityLog.LogType type, Pageable pageable);

    /**
     * Find logs by date range
     */
    List<ActivityLog> findAllByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find audit logs (type = LOG) by action
     */
    List<ActivityLog> findAllByTypeAndAction(ActivityLog.LogType type, ActivityLog.ActionType action);
}
