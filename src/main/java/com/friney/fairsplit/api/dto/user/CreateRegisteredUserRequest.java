package com.friney.fairsplit.api.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CreateRegisteredUserRequest(
        @NotBlank String name,
        @Email String email,
        @NotBlank String password,
        @NotBlank String confirmPassword
) {
}
