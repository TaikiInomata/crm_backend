package com.MD.CRM.service;

import com.MD.CRM.dto.CustomerRequestDTO;
import com.MD.CRM.dto.CustomerResponseDTO;
import com.MD.CRM.entity.Customer;
import com.MD.CRM.mapper.CustomerMapper;
import com.MD.CRM.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    /**
     * Add a new customer to the system
     * @param requestDTO the customer data
     * @param createdBy the ID of the user creating the customer
     * @return CustomerResponseDTO with the saved customer information
     * @throws IllegalArgumentException if email or phone number already exists
     */
    @Transactional
    public CustomerResponseDTO addCustomer(CustomerRequestDTO requestDTO, String createdBy) {
        // Check for duplicate email (only if email is provided)
        if (requestDTO.getEmail() != null && !requestDTO.getEmail().isEmpty()) {
            if (customerRepository.existsByEmailAndDeletedAtIsNull(requestDTO.getEmail())) {
                throw new IllegalArgumentException("Email already exists: " + requestDTO.getEmail());
            }
        }

        // Check for duplicate phone number (only if phone is provided)
        if (requestDTO.getPhone() != null && !requestDTO.getPhone().isEmpty()) {
            if (customerRepository.existsByPhoneAndDeletedAtIsNull(requestDTO.getPhone())) {
                throw new IllegalArgumentException("Phone number already exists: " + requestDTO.getPhone());
            }
        }

        // Convert DTO to entity
        Customer customer = customerMapper.toEntity(requestDTO);
        customer.setCreatedBy(createdBy);

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
}
