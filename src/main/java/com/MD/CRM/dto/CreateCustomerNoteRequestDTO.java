package com.MD.CRM.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCustomerNoteRequestDTO {
    @NotBlank(message = "Customer Id is required!")
    @NotNull
    private String customerId;

    @NotBlank(message = "User Id is required!")
    @NotNull
    private String userId;

    @NotBlank(message = "Content is required!")
    @NotNull
    private String content;
}
