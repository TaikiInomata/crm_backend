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
    final private com.MD.CRM.service.CustomerService customerService;
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
        customerNote.setStatus(true);

        // 5️⃣ Lưu vào DB
        CustomerNote savedNote = customerNoteRepository.save(customerNote);

        // 6️⃣ Trả về DTO phản hồi
        // Record activity
        try {
            String customerName = customer.getFullname();
            String desc = "Created note for customer='" + (customerName == null ? "" : customerName) + "' (id=" + request.getCustomerId() + ")";
            // pass null for type so ActivityLogService resolves type from action (CREATE -> LOG)
            activityLogService.record(request.getUserId(), null, com.MD.CRM.entity.ActivityAction.CREATE, desc);
        } catch (Exception ignore) {
        }
        return customerNoteMapper.toResponseDTO(savedNote);

    }

    public CustomerNoteResponseDTO update(@RequestBody UpdateCustomerNoteRequestDTO request, String id, String userId) {
        CustomerNote note = customerNoteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer note not found!"));

        if (note.getStaff() == null || !note.getStaff().getId().equals(userId)) {
            throw new IllegalArgumentException("You don't have permission to update this note");
        }
        note.setContent(request.getContent());

        CustomerNote savedNote = customerNoteRepository.save(note);

        try {
            String customerName = "";
            String customerId = note.getCustomer() == null ? null : note.getCustomer().getId();
            if (customerId != null) {
                try {
                    var cust = customerService.getCustomerById(customerId);
                    customerName = cust == null ? "" : cust.getFullname();
                } catch (Exception ignored) {
                }
            }
            String desc = "Updated note id=" + id + " for customer='" + (customerName == null ? "" : customerName) + "' (id=" + (customerId == null ? "" : customerId) + ")";
            activityLogService.record(note.getStaff() == null ? null : note.getStaff().getId(), null, com.MD.CRM.entity.ActivityAction.UPDATE, desc);
        } catch (Exception ignore) {
        }

        return customerNoteMapper.toResponseDTO(savedNote);
    }

    // DELETE
    public void delete(String id, String userId) {
        CustomerNote note = customerNoteRepository.findByIdAndStatusTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer note not found!"));

        if (note.getStaff() == null || !note.getStaff().getId().equals(userId)) {
            throw new IllegalArgumentException("You don't have permission to delete this note");
        }

        note.setStatus(false);
        customerNoteRepository.save(note);
        try {
            String customerName = "";
            String customerId = note.getCustomer() == null ? null : note.getCustomer().getId();
            if (customerId != null) {
                try {
                    var cust = customerService.getCustomerById(customerId);
                    customerName = cust == null ? "" : cust.getFullname();
                } catch (Exception ignored) {
                }
            }
            String desc = "Deleted note id=" + id + " for customer='" + (customerName == null ? "" : customerName) + "' (id=" + (customerId == null ? "" : customerId) + ")";
            activityLogService.record(note.getStaff() == null ? null : note.getStaff().getId(), null, com.MD.CRM.entity.ActivityAction.EDIT, desc);
        } catch (Exception ignore) {
        }
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
