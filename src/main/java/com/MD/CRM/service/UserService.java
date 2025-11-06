package com.MD.CRM.service;

import com.MD.CRM.dto.UpdateRoleDTO;
import com.MD.CRM.dto.UserRequestDTO;
import com.MD.CRM.dto.UserResponseDTO;
import com.MD.CRM.dto.UserUpdateDTO;
import com.MD.CRM.entity.User;
import com.MD.CRM.exception.DuplicateResourceException;
import com.MD.CRM.exception.ResourceNotFoundException;
import com.MD.CRM.mapper.UserMapper;
import com.MD.CRM.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Create a new user
     * @param requestDTO user data
     * @return UserResponseDTO
     * @throws DuplicateResourceException if username or email already exists
     */
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO requestDTO) {
        log.info("Creating new user with username: {}", requestDTO.getUsername());

        // Check for duplicate username
        if (userRepository.existsByUsername(requestDTO.getUsername())) {
            log.error("Username already exists: {}", requestDTO.getUsername());
            throw new DuplicateResourceException("Username already exists: " + requestDTO.getUsername());
        }

        // Check for duplicate email
        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            log.error("Email already exists: {}", requestDTO.getEmail());
            throw new DuplicateResourceException("Email already exists: " + requestDTO.getEmail());
        }

        // Convert DTO to entity
        User user = userMapper.toEntity(requestDTO);
        
        // TODO: Hash password before saving (use BCryptPasswordEncoder)
        // For now, storing plain text - MUST BE FIXED IN PRODUCTION
        
        // Set timestamps manually (in case @CreationTimestamp/@UpdateTimestamp doesn't work)
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        
        // Save user
        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());

        return userMapper.toResponseDTO(savedUser);
    }

    /**
     * Update user information
     * @param id user ID
     * @param updateDTO updated user data
     * @return UserResponseDTO
     * @throws ResourceNotFoundException if user not found
     * @throws DuplicateResourceException if email already exists
     */
    @Transactional
    public UserResponseDTO updateUser(String id, UserUpdateDTO updateDTO) {
        log.info("Updating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });

        // Check if user is active
        if (!user.getIsActive()) {
            log.error("Cannot update deactivated user with ID: {}", id);
            throw new IllegalArgumentException("Cannot update deactivated user");
        }

        // Update email if provided and different
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateDTO.getEmail())) {
                log.error("Email already exists: {}", updateDTO.getEmail());
                throw new DuplicateResourceException("Email already exists: " + updateDTO.getEmail());
            }
            user.setEmail(updateDTO.getEmail());
        }

        // Update fullname if provided
        if (updateDTO.getFullname() != null) {
            user.setFullname(updateDTO.getFullname());
        }

        // Update password if provided
        if (updateDTO.getPassword() != null && !updateDTO.getPassword().isEmpty()) {
            // TODO: Hash password before saving
            user.setPassword(updateDTO.getPassword());
        }

        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with ID: {}", id);

        return userMapper.toResponseDTO(updatedUser);
    }

    /**
     * Update user role (Admin only)
     * @param id user ID
     * @param roleDTO new role
     * @return UserResponseDTO
     * @throws ResourceNotFoundException if user not found
     */
    @Transactional
    public UserResponseDTO updateRole(String id, UpdateRoleDTO roleDTO) {
        log.info("Updating role for user with ID: {} to {}", id, roleDTO.getRole());

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });

        user.setRole(roleDTO.getRole());
        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);
        log.info("Role updated successfully for user ID: {}", id);

        return userMapper.toResponseDTO(updatedUser);
    }

    /**
     * Deactivate user account
     * @param id user ID
     * @throws ResourceNotFoundException if user not found
     */
    @Transactional
    public void deactivateUser(String id) {
        log.info("Deactivating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });

        if (!user.getIsActive()) {
            log.warn("User is already deactivated with ID: {}", id);
            throw new IllegalArgumentException("User is already deactivated");
        }

        user.setIsActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        log.info("User deactivated successfully with ID: {}", id);
    }

    /**
     * Reactivate user account
     * @param id user ID
     * @throws ResourceNotFoundException if user not found
     */
    @Transactional
    public UserResponseDTO reactivateUser(String id) {
        log.info("Reactivating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });

        if (user.getIsActive()) {
            log.warn("User is already active with ID: {}", id);
            throw new IllegalArgumentException("User is already active");
        }

        user.setIsActive(true);
        user.setUpdatedAt(LocalDateTime.now());
        User reactivatedUser = userRepository.save(user);
        log.info("User reactivated successfully with ID: {}", id);

        return userMapper.toResponseDTO(reactivatedUser);
    }

    /**
     * Get all users with pagination and filtering
     * @param keyword search keyword (optional)
     * @param isActive filter by active status (optional)
     * @param page page number
     * @param size page size
     * @return Map with pagination info
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAllUsers(String keyword, Boolean isActive, int page, int size) {
        log.info("Getting all users - page: {}, size: {}, keyword: {}, isActive: {}", page, size, keyword, isActive);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> userPage;

        if (keyword != null && !keyword.isEmpty()) {
            userPage = userRepository.searchUsers(keyword, pageable);
        } else if (isActive != null) {
            userPage = userRepository.findAllByIsActive(isActive, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        List<UserResponseDTO> userDTOs = userPage.getContent().stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("users", userDTOs);
        response.put("currentPage", userPage.getNumber());
        response.put("totalItems", userPage.getTotalElements());
        response.put("totalPages", userPage.getTotalPages());
        response.put("pageSize", userPage.getSize());

        log.info("Retrieved {} users", userDTOs.size());
        return response;
    }

    /**
     * Get user by ID
     * @param id user ID
     * @return UserResponseDTO
     * @throws ResourceNotFoundException if user not found
     */
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(String id) {
        log.info("Getting user by ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });

        return userMapper.toResponseDTO(user);
    }

    /**
     * Get all users by role
     * @param role user role
     * @return List of UserResponseDTO
     */
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getUsersByRole(User.Role role) {
        log.info("Getting users by role: {}", role);

        List<User> users = userRepository.findAllByRole(role);
        return users.stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
