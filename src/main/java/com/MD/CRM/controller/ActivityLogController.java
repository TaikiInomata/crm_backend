package com.MD.CRM.controller;

import com.MD.CRM.dto.ActivityLogDTO;
import com.MD.CRM.service.ActivityLogService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @GetMapping("/logs")
    @Operation(summary = "Search activity logs")
    public ResponseEntity<?> searchLogs(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        var results = activityLogService.search(userId, action, from, to, pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping(value = "/export", produces = "text/csv")
    @Operation(summary = "Export logs to CSV")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        // fetch first page with large size for export
        var page = activityLogService.search(userId, action, from, to, PageRequest.of(0, 10000));
        List<ActivityLogDTO> logs = page.getContent();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);
        writer.println("id,userId,username,action,description,createdAt");
        for (ActivityLogDTO l : logs) {
            writer.printf("%s,%s,%s,%s,%s,%s\n",
                    l.getId(),
                    l.getUserId(),
                    l.getUsername(),
                    l.getAction(),
                    l.getDescription() == null ? "" : l.getDescription().replaceAll("\r|\n", " "),
                    l.getCreatedAt() == null ? "" : l.getCreatedAt().toString()
            );
        }
        writer.flush();

        byte[] csv = out.toByteArray();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=activity_logs.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}
