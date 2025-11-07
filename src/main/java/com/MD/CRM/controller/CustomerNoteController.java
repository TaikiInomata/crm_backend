package com.MD.CRM.controller;

import com.MD.CRM.dto.CreateCustomerNoteRequestDTO;
import com.MD.CRM.dto.CustomerNoteResponseDTO;
import com.MD.CRM.dto.UpdateCustomerNoteRequestDTO;
import com.MD.CRM.service.CustomerNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/customer-notes")
@RequiredArgsConstructor
public class CustomerNoteController {

    private final CustomerNoteService customerNoteService;

    // 🟩 CREATE
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> create(@RequestBody CreateCustomerNoteRequestDTO request) {
        Map<String, Object> response = new HashMap<>();
        try {
            CustomerNoteResponseDTO data = customerNoteService.create(request);
            response.put("message", "Created Customer Note Successfully!");
            response.put("data", data);
            response.put("success", true);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            response.put("success", false);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("message", "An error occurred while creating the note");
            response.put("error", e.getMessage());
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 🟦 UPDATE
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> update(@RequestBody UpdateCustomerNoteRequestDTO request,
                                                      @PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            CustomerNoteResponseDTO data = customerNoteService.update(request, id);
            response.put("message", "Updated Customer Note Successfully!");
            response.put("data", data);
            response.put("success", true);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            response.put("success", false);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("message", "An error occurred while updating the note");
            response.put("error", e.getMessage());
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 🟥 DELETE
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            customerNoteService.delete(id);
            response.put("message", "Deleted Customer Note Successfully!");
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            response.put("success", false);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("message", "An error occurred while deleting the note");
            response.put("error", e.getMessage());
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 🟨 GET ALL (có phân trang)
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllNotes(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String staffId,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        Map<String, Object> response = new HashMap<>();

        try {
            Page<CustomerNoteResponseDTO> data = customerNoteService.getAll(customerId, staffId, pageable);

            response.put("message", "Fetched customer notes successfully!");
            response.put("data", data);
            response.put("success", true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Failed to fetch customer notes");
            response.put("error", e.getMessage());
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    // 🟧 GET DETAIL
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getDetail(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            CustomerNoteResponseDTO data = customerNoteService.getDetail(id);
            response.put("message", "Fetched Customer Note Successfully!");
            response.put("data", data);
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            response.put("success", false);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("message", "An error occurred while fetching the note");
            response.put("error", e.getMessage());
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
