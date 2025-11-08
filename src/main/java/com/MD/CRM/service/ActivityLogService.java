package com.MD.CRM.service;

import com.MD.CRM.dto.ActivityLogDTO;
import com.MD.CRM.entity.ActivityLog;
import com.MD.CRM.entity.ActivityAction;
import com.MD.CRM.entity.ActivityType;
import com.MD.CRM.entity.User;
import com.MD.CRM.mapper.ActivityLogMapper;
import com.MD.CRM.repository.ActivityLogRepository;
import com.MD.CRM.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final ActivityLogMapper activityLogMapper;
    private final UserRepository userRepository;
    private final com.MD.CRM.repository.CustomerRepository customerRepository;

    public void record(String userId, ActivityType type, ActivityAction action, String description) {
    // If we don't have a user id, skip recording to avoid inserting null FK
    if (userId == null) return;

    User user = userRepository.findById(userId).orElse(null);
    // If user not found, skip recording
    if (user == null) return;
    // If caller didn't provide a type, derive it from the action
    ActivityType resolvedType = type;
    if (resolvedType == null && action != null) {
        resolvedType = action.getType();
    }

    ActivityLog log = ActivityLog.builder()
        .user(user)
        .type(resolvedType)
        .action(action)
        .description(description)
        .build();
    activityLogRepository.save(log);
    }

    public Page<ActivityLogDTO> search(String userId, ActivityType type, ActivityAction action, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        Page<ActivityLog> page = activityLogRepository.findByFilters(userId, type, action, from, to, pageable);
        return page.map(e -> {
            var dto = activityLogMapper.toDTO(e);
            // Try to extract customer id from description like "(id=...)", then fetch email
            if (dto != null && dto.getDescription() != null) {
                String desc = dto.getDescription();
                int idx = desc.indexOf("(id=");
                if (idx >= 0) {
                    int start = idx + 4;
                    int end = desc.indexOf(')', start);
                    if (end > start) {
                        String customerId = desc.substring(start, end);
                        try {
                            var cust = customerRepository.findById(customerId).orElse(null);
                            if (cust != null) dto.setCustomerEmail(cust.getEmail());
                        } catch (Exception ignored) {}
                    }
                }
            }
            return dto;
        });
    }
}
