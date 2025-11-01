package com.MD.CRM.service;

import com.MD.CRM.dto.CustomerRequestDTO;
import com.MD.CRM.dto.CustomerResponseDTO;
import com.MD.CRM.entity.Customer;
import com.MD.CRM.exception.DuplicateResourceException;
import com.MD.CRM.exception.ResourceNotFoundException;
import com.MD.CRM.mapper.CustomerMapper;
import com.MD.CRM.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    /**
     * Get all customers with pagination and optional keyword search
     * @param keyword optional search keyword (searches in fullname, email, phone, address)
     * @param page page number (0-indexed)
     * @param size number of items per page
     * @return Map containing pagination info and list of customers
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAllCustomers(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> customerPage = customerRepository.searchCustomers(keyword, pageable);

        List<CustomerResponseDTO> customerDTOs = customerPage.getContent().stream()
                .map(customerMapper::toResponseDTO)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("customers", customerDTOs);
        response.put("currentPage", customerPage.getNumber());
        response.put("totalItems", customerPage.getTotalElements());
        response.put("totalPages", customerPage.getTotalPages());
        response.put("pageSize", customerPage.getSize());

        return response;
    }

    /**
     * Get 5 most recent customers
     * @return List of 5 recent customers ordered by creation date
     */
    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> getRecentCustomers() {
        List<Customer> recentCustomers = customerRepository.findTop5ByDeletedAtIsNullOrderByCreatedAtDesc();
        
        return recentCustomers.stream()
                .map(customerMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get customer by ID
     * @param id the customer ID
     * @return CustomerResponseDTO
     * @throws ResourceNotFoundException if customer not found
     */
    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerById(String id) {
        Customer customer = customerRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        
        return customerMapper.toResponseDTO(customer);
    }

    /**
     * Add a new customer to the system
     * @param requestDTO the customer data
     * @param createdBy the ID of the user creating the customer
     * @return CustomerResponseDTO with the saved customer information
     * @throws DuplicateResourceException if email or phone number already exists
     */
    @Transactional
    public CustomerResponseDTO addCustomer(CustomerRequestDTO requestDTO, String createdBy) {
        // Check for duplicate email
        if (customerRepository.existsByEmailAndDeletedAtIsNull(requestDTO.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + requestDTO.getEmail());
        }

        // Check for duplicate phone number
        if (customerRepository.existsByPhoneAndDeletedAtIsNull(requestDTO.getPhone())) {
            throw new DuplicateResourceException("Phone number already exists: " + requestDTO.getPhone());
        }

        // Convert DTO to entity
        Customer customer = customerMapper.toEntity(requestDTO);
        customer.setCreatedBy(createdBy);
        
        // Set timestamps manually (in case @CreationTimestamp doesn't work)
        LocalDateTime now = LocalDateTime.now();
        customer.setCreatedAt(now);
        customer.setUpdatedAt(now);

        // Save to database
        Customer savedCustomer = customerRepository.save(customer);

        // Convert entity back to response DTO
        return customerMapper.toResponseDTO(savedCustomer);
    }

    /**
     * Update an existing customer
     * @param id the customer id
     * @param requestDTO the updated fields (validated)
     * @return updated CustomerResponseDTO
     * @throws IllegalArgumentException if email or phone duplicates another record
     */
    @Transactional
    public CustomerResponseDTO updateCustomer(String id, CustomerRequestDTO requestDTO) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + id));

        // Do not allow updating a soft-deleted record
        if (customer.getDeletedAt() != null) {
            throw new IllegalArgumentException("Customer not found with id: " + id);
        }

        // Require email on update
        if (requestDTO.getEmail() == null || requestDTO.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        // Check duplicate email (if provided)
        if (requestDTO.getEmail() != null && !requestDTO.getEmail().isEmpty()) {
            customerRepository.findByEmailAndDeletedAtIsNull(requestDTO.getEmail()).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new IllegalArgumentException("Email already exists: " + requestDTO.getEmail());
                }
            });
        }

        // Check duplicate phone (if provided)
        if (requestDTO.getPhone() != null && !requestDTO.getPhone().isEmpty()) {
            customerRepository.findByPhoneAndDeletedAtIsNull(requestDTO.getPhone()).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new IllegalArgumentException("Phone number already exists: " + requestDTO.getPhone());
                }
            });
        }

        // Map fields from request to entity
        customer.setFullname(requestDTO.getFullname());
        customer.setEmail(requestDTO.getEmail());
        customer.setPhone(requestDTO.getPhone());
        customer.setAddress(requestDTO.getAddress());
        customer.setDescription(requestDTO.getDescription());

        Customer saved = customerRepository.save(customer);
        return customerMapper.toResponseDTO(saved);
    }

    /**
     * Search customers by keyword with paging and simple relevance ordering.
     * Keyword is matched case-insensitively against fullname and email, and raw against phone.
     */
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public org.springframework.data.domain.Page<com.MD.CRM.dto.CustomerResponseDTO> searchCustomers(String keyword, int page, int size) {
        String q = keyword == null ? "" : keyword.toLowerCase();
        String qRaw = keyword == null ? "" : keyword;
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);

        org.springframework.data.domain.Page<Customer> results = customerRepository.searchByKeyword(q, qRaw, pageable);

        return results.map(customerMapper::toResponseDTO);
    }
}
