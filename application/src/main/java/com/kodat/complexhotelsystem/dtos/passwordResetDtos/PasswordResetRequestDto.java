package com.kodat.complexhotelsystem.dtos.passwordResetDtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequestDto (
        @NotBlank(message = "Email cannot be empty!")
        @Email(message = "Invalid email!")
        String email
){

}
