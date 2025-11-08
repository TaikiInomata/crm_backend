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

    public void record(String userId, ActivityType type, ActivityAction action, String description) {
    // If we don't have a user id, skip recording to avoid inserting null FK
    if (userId == null) return;

    User user = userRepository.findById(userId).orElse(null);
    // If user not found, skip recording
    if (user == null) return;

    ActivityLog log = ActivityLog.builder()
        .user(user)
        .type(type)
        .action(action)
        .description(description)
        .build();
    activityLogRepository.save(log);
    }

    public Page<ActivityLogDTO> search(String userId, ActivityType type, ActivityAction action, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        Page<ActivityLog> page = activityLogRepository.findByFilters(userId, type, action, from, to, pageable);
        return page.map(activityLogMapper::toDTO);
    }
}
