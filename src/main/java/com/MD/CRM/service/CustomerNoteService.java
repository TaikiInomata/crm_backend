package com.MD.CRM.service;

import com.MD.CRM.dto.CreateCustomerNoteRequestDTO;
import com.MD.CRM.dto.CustomerNoteResponseDTO;
import com.MD.CRM.dto.UpdateCustomerNoteRequestDTO;
import com.MD.CRM.entity.Customer;
import com.MD.CRM.entity.CustomerNote;
import com.MD.CRM.entity.User;
import com.MD.CRM.mapper.CustomerNoteMapper;
import com.MD.CRM.repository.CustomerNoteRepository;
import com.MD.CRM.repository.CustomerRepository;
import com.MD.CRM.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class CustomerNoteService {
    final private CustomerNoteRepository customerNoteRepository;
    final private CustomerNoteMapper customerNoteMapper;
    final private CustomerRepository customerRepository;
    final private UserRepository userRepository;
    final private com.MD.CRM.service.ActivityLogService activityLogService;

    public CustomerNoteResponseDTO create(CreateCustomerNoteRequestDTO request) {
        // 1️⃣ Kiểm tra tồn tại Customer
        Customer customer = customerRepository.findByIdAndDeletedAtIsNull(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found or deleted!"));

        // 2️⃣ Kiểm tra tồn tại Staff/User
        User staff = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Staff not found!"));

        // 3️⃣ Map từ DTO sang Entity
        CustomerNote customerNote = customerNoteMapper.toEntity(request);

        // 4️⃣ Gán lại quan hệ chính xác (nếu mapper chưa set)
        customerNote.setCustomer(customer);
        customerNote.setStaff(staff);

        // 5️⃣ Lưu vào DB
        CustomerNote savedNote = customerNoteRepository.save(customerNote);

        // 6️⃣ Trả về DTO phản hồi
        // Record activity
        try {
            activityLogService.record(request.getUserId(), "NOTE_CREATE", "Created note for customerId=" + request.getCustomerId());
        } catch (Exception ignore) {}
        return customerNoteMapper.toResponseDTO(savedNote);

    }

    public CustomerNoteResponseDTO update(@RequestBody UpdateCustomerNoteRequestDTO request, String id) {
        CustomerNote note = customerNoteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer note not found!"));
        note.setContent(request.getContent());

        CustomerNote savedNote = customerNoteRepository.save(note);

        try {
            activityLogService.record(note.getStaff() == null ? null : note.getStaff().getId(), "NOTE_UPDATE", "Updated note id=" + id);
        } catch (Exception ignore) {}

        return customerNoteMapper.toResponseDTO(savedNote);
    }

    // DELETE
    public void delete(String id) {
        CustomerNote note = customerNoteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer note not found!"));

        customerNoteRepository.delete(note);
    }

    // GET DETAIL
    public CustomerNoteResponseDTO getDetail(String id) {
        CustomerNote note = customerNoteRepository.findByIdAndStatusTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer note not found!"));

        return customerNoteMapper.toResponseDTO(note);
    }

    // GET ALL
    public Page<CustomerNoteResponseDTO> getAll(String customerId, String staffId, Pageable pageable) {
        Page<CustomerNote> notes = customerNoteRepository.findByFilters(customerId, staffId, pageable);
        return notes.map(customerNoteMapper::toResponseDTO);
    }


}
