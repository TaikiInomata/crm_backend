package com.MD.CRM.dto;

import com.MD.CRM.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRoleDTO {

    @NotNull(message = "Role is required")
    private User.Role role;
}
