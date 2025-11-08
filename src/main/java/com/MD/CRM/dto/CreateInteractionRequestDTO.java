package com.MD.CRM.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateInteractionRequestDTO {
    private String customerEmail;
    private String userEmail;
    private String type; // optional, default INTERACTION
    private String action; // CALL, EMAIL, MEETING, OTHER
    private String description;
}
