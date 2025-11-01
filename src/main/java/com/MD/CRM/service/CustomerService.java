package com.MD.CRM.service;

import com.MD.CRM.dto.CustomerRequestDTO;
import com.MD.CRM.dto.CustomerResponseDTO;
import com.MD.CRM.entity.Customer;
import com.MD.CRM.exception.DuplicateResourceException;
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

        // Save to database
        Customer savedCustomer = customerRepository.save(customer);

        // Convert entity back to response DTO
        return customerMapper.toResponseDTO(savedCustomer);
    }
}
