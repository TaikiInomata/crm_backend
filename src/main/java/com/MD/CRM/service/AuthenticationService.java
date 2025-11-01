package com.MD.CRM.service;

import com.MD.CRM.dto.AuthenticationRequestDTO;
import com.MD.CRM.dto.AuthenticationResponseDTO;
import com.MD.CRM.entity.User;
import com.MD.CRM.repository.UserRepository;
import com.MD.CRM.utils.JwtUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import org.mindrot.jbcrypt.BCrypt;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    UserRepository userRepository;
    JwtUtil jwtUtil;


    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request) {

        // 1️⃣ Kiểm tra dữ liệu đầu vào
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required!");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required!");
        }

        // 2️⃣ Tìm user theo email + active = true
        User user = userRepository.findByEmailAndIsActiveTrue(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email not found or user is inactive!"));

        // 3️⃣ Kiểm tra mật khẩu
        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect password!");
        }

        // 4️⃣ Sinh JWT access & refresh token
        String accessToken = jwtUtil.generateToken(user.getEmail(), false);
        String refreshToken = jwtUtil.generateToken(user.getEmail(), true);

        // 5️⃣ Trả về response
        return AuthenticationResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String encodePassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(10));
    }
}
