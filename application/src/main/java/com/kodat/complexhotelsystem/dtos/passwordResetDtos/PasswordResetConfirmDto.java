package com.kodat.complexhotelsystem.dtos.passwordResetDtos;

public record PasswordResetConfirmDto(String code, String email, String password) {
}
