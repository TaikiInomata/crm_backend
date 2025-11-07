package com.MD.CRM.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticationResponseDTO {
    private String id;
    private String accessToken;
    private String refreshToken;


}
