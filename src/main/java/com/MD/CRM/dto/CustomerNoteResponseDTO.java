package com.MD.CRM.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerNoteResponseDTO {
    private String id;
    private String customerName;
    private String staffName;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
