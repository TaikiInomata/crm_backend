package com.MD.CRM.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLog {

    @Id
    @UuidGenerator
    @Column(length = 255)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 255)
    private String action; // e.g., CUSTOMER_CREATE, NOTE_UPDATE, LOGIN

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
