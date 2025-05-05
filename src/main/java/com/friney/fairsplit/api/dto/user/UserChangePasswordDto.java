package com.friney.fairsplit.api.dto.user;

import lombok.Builder;

@Builder
public record UserChangePasswordDto(
        String oldPassword,
        String newPassword,
        String confirmPassword
) {
}
