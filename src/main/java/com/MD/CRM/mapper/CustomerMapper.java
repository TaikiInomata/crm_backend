package com.MD.CRM.mapper;

import com.MD.CRM.dto.CustomerRequestDTO;
import com.MD.CRM.dto.CustomerResponseDTO;
import com.MD.CRM.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    /**
     * Convert CustomerRequestDTO to Customer entity
     * @param requestDTO the request DTO
     * @return Customer entity
     */
    public Customer toEntity(CustomerRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        Customer customer = new Customer();
        customer.setFullname(requestDTO.getFullname());
        customer.setEmail(requestDTO.getEmail());
        customer.setPhone(requestDTO.getPhone());
        customer.setAddress(requestDTO.getAddress());
        customer.setDescription(requestDTO.getDescription());

        return customer;
    }

    /**
     * Convert Customer entity to CustomerResponseDTO
     * @param customer the entity
     * @return CustomerResponseDTO
     */
    public CustomerResponseDTO toResponseDTO(Customer customer) {
        if (customer == null) {
            return null;
        }

        CustomerResponseDTO responseDTO = new CustomerResponseDTO();
        responseDTO.setId(customer.getId());
        responseDTO.setFullname(customer.getFullname());
        responseDTO.setEmail(customer.getEmail());
        responseDTO.setPhone(customer.getPhone());
        responseDTO.setAddress(customer.getAddress());
        responseDTO.setDescription(customer.getDescription());
        responseDTO.setCreatedBy(customer.getCreatedBy());
        responseDTO.setCreatedAt(customer.getCreatedAt());
        responseDTO.setUpdatedAt(customer.getUpdatedAt());

        return responseDTO;
    }
}
