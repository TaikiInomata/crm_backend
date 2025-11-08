package com.MD.CRM.mapper;

import com.MD.CRM.dto.CreateCustomerNoteRequestDTO;
import com.MD.CRM.dto.CustomerNoteResponseDTO;
import com.MD.CRM.entity.CustomerNote;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerNoteMapper {

    public CustomerNote toEntity(CreateCustomerNoteRequestDTO requestDTO) {
        return CustomerNote.builder()
                .content(requestDTO.getContent())
                .build();
    }

    public CustomerNoteResponseDTO toResponseDTO(CustomerNote entity) {

        return CustomerNoteResponseDTO.builder()
                .id(entity.getId())
                .customerName(entity.getCustomer().getFullname())
                .staffName(entity.getStaff().getFullname())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
