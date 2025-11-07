package com.MD.CRM.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateCustomerNoteRequestDTO {
    private String content;
}
