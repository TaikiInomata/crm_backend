package com.MD.CRM.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLogDTO {
    private String id;
    private String userId;
    private String username;
    private String action;
    private String type;
    private String description;
    private LocalDateTime createdAt;
}
