package com.MD.CRM.controller;

import com.MD.CRM.dto.UpdateRoleDTO;
import com.MD.CRM.dto.UserRequestDTO;
import com.MD.CRM.dto.UserResponseDTO;
import com.MD.CRM.dto.UserUpdateDTO;
import com.MD.CRM.entity.User;
import com.MD.CRM.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing staff accounts (Admin only)")
public class UserController {

    private final UserService userService;

    /**
     * Create a new user
     * @param requestDTO user data
     * @return ResponseEntity with created user
     */
    @PostMapping
    @Operation(summary = "Create new user", description = "Create a new staff account (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin only"),
            @ApiResponse(responseCode = "409", description = "Conflict - username or email already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody UserRequestDTO requestDTO) {
        try {
            UserResponseDTO user = userService.createUser(requestDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User created successfully");
            response.put("data", user);
            response.put("success", true);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "An error occurred while creating the user");
            errorResponse.put("error", e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get all users with pagination and filtering
     * @param keyword search keyword (optional)
     * @param isActive filter by active status (optional)
     * @param page page number (default: 0)
     * @param size page size (default: 20)
     * @return ResponseEntity with paginated users
     */
    @GetMapping
    @Operation(summary = "Get all users with pagination", 
               description = "Get all users with pagination and optional filtering. " +
                           "Keyword will search across: username, email, and fullname fields (case-insensitive)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin only"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @io.swagger.v3.oas.annotations.Parameter(
                description = "Search keyword - searches in username, email, and fullname (case-insensitive)", 
                example = "admin"
            )
            @RequestParam(required = false) String keyword,
            @io.swagger.v3.oas.annotations.Parameter(
                description = "Filter by active status - true for active users, false for deactivated users", 
                example = "true"
            )
            @RequestParam(required = false) Boolean isActive,
            @io.swagger.v3.oas.annotations.Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @io.swagger.v3.oas.annotations.Parameter(description = "Number of items per page", example = "20")
            @RequestParam(defaultValue = "20") int size) {

        Map<String, Object> result = userService.getAllUsers(keyword, isActive, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Users retrieved successfully");
        response.put("data", result.get("users"));
        response.put("currentPage", result.get("currentPage"));
        response.put("totalItems", result.get("totalItems"));
        response.put("totalPages", result.get("totalPages"));
        response.put("pageSize", result.get("pageSize"));
        response.put("success", true);

        return ResponseEntity.ok(response);
    }

    /**
     * Get user by ID
     * @param id user ID
     * @return ResponseEntity with user details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Get a single user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin only"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable String id) {
        UserResponseDTO user = userService.getUserById(id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User retrieved successfully");
        response.put("data", user);
        response.put("success", true);

        return ResponseEntity.ok(response);
    }

    /**
     * Get users by role
     * @param role user role (ADMIN or STAFF)
     * @return ResponseEntity with list of users
     */
    @GetMapping("/role/{role}")
    @Operation(summary = "Get users by role", description = "Get all users by role (ADMIN or STAFF)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin only"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, Object>> getUsersByRole(@PathVariable User.Role role) {
        List<UserResponseDTO> users = userService.getUsersByRole(role);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Users retrieved successfully");
        response.put("data", users);
        response.put("total", users.size());
        response.put("success", true);

        return ResponseEntity.ok(response);
    }

    /**
     * Update user information
     * @param id user ID
     * @param updateDTO updated user data
     * @return ResponseEntity with updated user
     */
    @PatchMapping("/{id}")
    @Operation(summary = "Update user", description = "Update user information (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed or user is deactivated"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin only"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Conflict - email already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        try {
            UserResponseDTO updatedUser = userService.updateUser(id, updateDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User updated successfully");
            response.put("data", updatedUser);
            response.put("success", true);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "An error occurred while updating the user");
            errorResponse.put("error", e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Update user role
     * @param id user ID
     * @param roleDTO new role
     * @return ResponseEntity with updated user
     */
    @PatchMapping("/{id}/role")
    @Operation(summary = "Update user role", description = "Update user role to ADMIN or STAFF (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin only"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, Object>> updateRole(
            @PathVariable String id,
            @Valid @RequestBody UpdateRoleDTO roleDTO) {
        try {
            UserResponseDTO updatedUser = userService.updateRole(id, roleDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User role updated successfully");
            response.put("data", updatedUser);
            response.put("success", true);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "An error occurred while updating the role");
            errorResponse.put("error", e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Deactivate user account
     * @param id user ID
     * @return ResponseEntity with success message
     */
    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate user", description = "Deactivate user account (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deactivated successfully"),
            @ApiResponse(responseCode = "400", description = "User is already deactivated"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin only"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, Object>> deactivateUser(@PathVariable String id) {
        try {
            userService.deactivateUser(id);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User deactivated successfully");
            response.put("success", true);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "An error occurred while deactivating the user");
            errorResponse.put("error", e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Reactivate user account
     * @param id user ID
     * @return ResponseEntity with reactivated user
     */
    @PatchMapping("/{id}/reactivate")
    @Operation(summary = "Reactivate user", description = "Reactivate deactivated user account (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User reactivated successfully"),
            @ApiResponse(responseCode = "400", description = "User is already active"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin only"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, Object>> reactivateUser(@PathVariable String id) {
        try {
            UserResponseDTO reactivatedUser = userService.reactivateUser(id);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User reactivated successfully");
            response.put("data", reactivatedUser);
            response.put("success", true);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "An error occurred while reactivating the user");
            errorResponse.put("error", e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
