package com.kodat.complexhotelsystem.dtos.passwordResetDtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordResetConfirmDto(
        @NotBlank(message = "Reset code cant be empty!")
        @Size(min = 6, max = 6, message = "Reset code has to have 6 numbers!")
        String code,
        @NotBlank(message = "Email cant be empty!")
        @Email(message = "Invalid email!")
        String email,
        @Size(min = 12, max = 128, message = "Password has to have more then 12 and less then 128 letters!")
        @NotBlank(message = "New password cant be empty!")
        String password
) {
}
