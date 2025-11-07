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
    @Convert(converter = com.MD.CRM.converter.ActivityActionConverter.class)
    @Column(nullable = false, length = 50)
    private ActivityAction action;

    @Convert(converter = com.MD.CRM.converter.ActivityTypeConverter.class)
    @Column(nullable = false, length = 50)
    private ActivityType type;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
