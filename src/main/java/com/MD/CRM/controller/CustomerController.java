package com.MD.CRM.controller;

import com.MD.CRM.dto.CustomerRequestDTO;
import com.MD.CRM.dto.CustomerResponseDTO;
import com.MD.CRM.service.CustomerService;
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
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "APIs for managing customer records")
public class CustomerController {

    private final CustomerService customerService;

    /**
     * Get all customers with pagination and optional keyword search
     * @param keyword optional search keyword (searches in fullname, email, phone, address)
     * @param page page number (default: 0)
     * @param size number of items per page (default: 20)
     * @return ResponseEntity with pagination info and list of customers
     */
    @GetMapping("/all")
    @Operation(summary = "Get all customers with pagination", 
               description = "Retrieves customers with pagination and optional keyword search (searches in fullname, email, phone, address)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved customer list"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, Object>> getAllCustomers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Map<String, Object> result = customerService.getAllCustomers(keyword, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Customers retrieved successfully");
        response.put("data", result.get("customers"));
        response.put("currentPage", result.get("currentPage"));
        response.put("totalItems", result.get("totalItems"));
        response.put("totalPages", result.get("totalPages"));
        response.put("pageSize", result.get("pageSize"));
        response.put("success", true);

        return ResponseEntity.ok(response);
    }

    /**
     * Get 5 most recent customers
     * @return ResponseEntity with list of 5 recent customers
     */
    @GetMapping("/recent")
    @Operation(summary = "Get recent customers", description = "Retrieves 5 most recent customers ordered by creation date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved recent customers"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, Object>> getRecentCustomers() {
        List<CustomerResponseDTO> recentCustomers = customerService.getRecentCustomers();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Recent customers retrieved successfully");
        response.put("data", recentCustomers);
        response.put("success", true);

        return ResponseEntity.ok(response);
    }

    /**
     * Get customer by ID
     * @param id the customer UUID
     * @return ResponseEntity with customer details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", description = "Retrieves a single customer by UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved customer"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, Object>> getCustomerById(@PathVariable String id) {
        CustomerResponseDTO customer = customerService.getCustomerById(id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Customer retrieved successfully");
        response.put("data", customer);
        response.put("success", true);

        return ResponseEntity.ok(response);
    }

    /**
     * Add a new customer
     * @param requestDTO the customer data (fullname, email, phone are required)
     * @return ResponseEntity with success message and customer data
     */
    @PostMapping
    @Operation(summary = "Add a new customer", description = "Creates a new customer record in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed - missing or invalid required fields"),
            @ApiResponse(responseCode = "409", description = "Conflict - duplicate email or phone number"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, Object>> addCustomer(
            @Valid @RequestBody CustomerRequestDTO requestDTO) {
        // Tạm thời để createdBy = null, sau này sẽ lấy từ authentication context
        CustomerResponseDTO customer = customerService.addCustomer(requestDTO, null);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Customer added successfully");
        response.put("data", customer);
        response.put("success", true);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
