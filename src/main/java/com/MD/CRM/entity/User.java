package com.MD.CRM.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @UuidGenerator
    @Column(length = 255)
    private String id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(unique = true, length = 150)
    private String email;

    @Column(length = 150)
    private String fullname;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Role role = Role.STAFF;

    @Column(nullable = false)
    private Boolean isActive = true;

    private LocalDateTime lastLogin;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Enum Role (ADMIN / STAFF)
    public enum Role {
        ADMIN, STAFF
    }
}