package com.MD.CRM.mapper;

import com.MD.CRM.dto.ActivityLogDTO;
import com.MD.CRM.entity.ActivityLog;
import org.springframework.stereotype.Component;

@Component
public class ActivityLogMapper {

    public ActivityLogDTO toDTO(ActivityLog e) {
        if (e == null) return null;
    return ActivityLogDTO.builder()
                .id(e.getId())
                .userId(e.getUser() == null ? null : e.getUser().getId())
                .username(e.getUser() == null ? null : e.getUser().getUsername())
        .action(e.getAction() == null ? null : e.getAction().name())
        .type(e.getType() == null ? null : e.getType().name())
                .description(e.getDescription())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
