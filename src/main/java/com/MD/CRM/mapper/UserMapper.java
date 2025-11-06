package com.MD.CRM.mapper;

import com.MD.CRM.dto.UserRequestDTO;
import com.MD.CRM.dto.UserResponseDTO;
import com.MD.CRM.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    /**
     * Convert User entity to UserResponseDTO
     */
    public UserResponseDTO toResponseDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullname(user.getFullname())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * Convert UserRequestDTO to User entity (for creation)
     */
    public User toEntity(UserRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return User.builder()
                .username(dto.getUsername())
                .password(dto.getPassword()) // Should be hashed before saving
                .email(dto.getEmail())
                .fullname(dto.getFullname())
                .role(User.Role.STAFF) // Default role
                .isActive(true) // Active by default
                .build();
    }
}
