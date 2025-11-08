package com.MD.CRM.service;

import com.MD.CRM.entity.ActivityLog;
import com.MD.CRM.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    /**
     * Log an audit action (create, edit, update, login)
     * @param action the action type
     * @param userId user ID who performed the action
     * @param description description of the action
     */
    @Transactional
    public void logAuditAction(
            ActivityLog.ActionType action,
            String userId,
            String description) {
        
        try {
            ActivityLog activityLog = ActivityLog.builder()
                    .type(ActivityLog.LogType.LOG)
                    .action(action)
                    .userId(userId)
                    .description(description)
                    .createdAt(LocalDateTime.now())
                    .build();

            activityLogRepository.save(activityLog);
            log.info("Audit log created: {} by user {} (ID: {})", action, userId, activityLog.getId());
        } catch (Exception e) {
            log.error("Failed to create audit log: {} by user {}", action, userId, e);
        }
    }

    /**
     * Log an interaction (call, email, meeting, other)
     * @param action the interaction type
     * @param userId user ID who performed the interaction
     * @param customerId the customer ID
     * @param description description of the interaction
     * @param startAt start time of interaction
     * @param endAt end time of interaction
     */
    @Transactional
    public void logInteraction(
            ActivityLog.ActionType action,
            String userId,
            String customerId,
            String description,
            LocalDateTime startAt,
            LocalDateTime endAt) {
        
        try {
            ActivityLog activityLog = ActivityLog.builder()
                    .type(ActivityLog.LogType.INTERACTION)
                    .action(action)
                    .userId(userId)
                    .customerId(customerId)
                    .description(description)
                    .startAt(startAt)
                    .endAt(endAt)
                    .createdAt(LocalDateTime.now())
                    .build();

            activityLogRepository.save(activityLog);
            log.info("Interaction log created: {} with Customer {} by user {} (ID: {})", 
                action, customerId, userId, activityLog.getId());
        } catch (Exception e) {
            log.error("Failed to create interaction log: {} with Customer {} by user {}", 
                action, customerId, userId, e);
        }
    }

    /**
     * Log user creation
     */
    public void logUserCreated(String userId, String username, String performedBy) {
        logAuditAction(
            ActivityLog.ActionType.CREATE,
            performedBy,
            String.format("User '%s' created (ID: %s)", username, userId)
        );
    }

    /**
     * Log user update
     */
    public void logUserUpdated(String userId, String username, String performedBy) {
        logAuditAction(
            ActivityLog.ActionType.UPDATE,
            performedBy,
            String.format("User '%s' updated (ID: %s)", username, userId)
        );
    }

    /**
     * Log user role change
     */
    public void logUserRoleChanged(String userId, String username, String oldRole, String newRole, String performedBy) {
        logAuditAction(
            ActivityLog.ActionType.EDIT,
            performedBy,
            String.format("User '%s' (ID: %s) role changed from %s to %s", username, userId, oldRole, newRole)
        );
    }

    /**
     * Log user deactivation
     */
    public void logUserDeactivated(String userId, String username, String performedBy) {
        logAuditAction(
            ActivityLog.ActionType.UPDATE,
            performedBy,
            String.format("User '%s' deactivated (ID: %s)", username, userId)
        );
    }

    /**
     * Log user reactivation
     */
    public void logUserReactivated(String userId, String username, String performedBy) {
        logAuditAction(
            ActivityLog.ActionType.UPDATE,
            performedBy,
            String.format("User '%s' reactivated (ID: %s)", username, userId)
        );
    }

    /**
     * Log user login
     */
    public void logUserLogin(String userId, String email) {
        logAuditAction(
            ActivityLog.ActionType.LOGIN,
            userId,
            String.format("User '%s' logged in", email)
        );
    }

    /**
     * Log customer creation
     */
    public void logCustomerCreated(String customerId, String customerName, String performedBy) {
        logAuditAction(
            ActivityLog.ActionType.CREATE,
            performedBy,
            String.format("Customer '%s' created (ID: %s)", customerName, customerId)
        );
    }

    /**
     * Log customer update
     */
    public void logCustomerUpdated(String customerId, String customerName, String performedBy) {
        logAuditAction(
            ActivityLog.ActionType.UPDATE,
            performedBy,
            String.format("Customer '%s' updated (ID: %s)", customerName, customerId)
        );
    }

    /**
     * Log customer deletion
     */
    public void logCustomerDeleted(String customerId, String customerName, String performedBy) {
        logAuditAction(
            ActivityLog.ActionType.UPDATE,
            performedBy,
            String.format("Customer '%s' deleted (ID: %s)", customerName, customerId)
        );
    }

    /**
     * Log customer restoration
     */
    public void logCustomerRestored(String customerId, String customerName, String performedBy) {
        logAuditAction(
            ActivityLog.ActionType.UPDATE,
            performedBy,
            String.format("Customer '%s' restored (ID: %s)", customerName, customerId)
        );
    }
}
