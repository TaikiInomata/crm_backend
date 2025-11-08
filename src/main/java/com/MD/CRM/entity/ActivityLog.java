package com.MD.CRM.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLog {

    @Id
    @UuidGenerator
    @Column(length = 255)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LogType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ActionType action;

    @Column(length = 255, name = "user_id")
    private String userId; // User ID who performed the action

    @Column(length = 255, name = "customer_id")
    private String customerId; // Customer ID (for interactions)

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_at")
    private LocalDateTime startAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    // Enum for log type
    public enum LogType {
        INTERACTION,  // Interaction with customer (call, email, meeting, other)
        LOG          // Audit log (create, edit, update, login)
    }

    // Enum for action type
    public enum ActionType {
        // Audit log actions
        CREATE,
        EDIT,
        UPDATE,
        LOGIN,
        
        // Interaction actions
        CALL,
        EMAIL,
        MEETING,
        OTHER
    }
}
