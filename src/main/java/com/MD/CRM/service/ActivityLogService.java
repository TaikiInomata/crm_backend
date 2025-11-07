package com.MD.CRM.service;

import com.MD.CRM.dto.ActivityLogDTO;
import com.MD.CRM.entity.ActivityLog;
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

    public void record(String userId, String action, String description) {
        User user = userRepository.findById(userId).orElse(null);
        ActivityLog log = ActivityLog.builder()
                .user(user)
                .action(action)
                .description(description)
                .build();
        activityLogRepository.save(log);
    }

    public Page<ActivityLogDTO> search(String userId, String action, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        Page<ActivityLog> page = activityLogRepository.findByFilters(userId, action, from, to, pageable);
        return page.map(activityLogMapper::toDTO);
    }
}
