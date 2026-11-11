package com.kodat.complexhotelsystem.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterDto {
    @NotBlank
    @Size(min = 3, max = 30)
    @Pattern(regexp = "^[\\p{L}\\p{M}0-9_-]+$")
    private String firstname;
    @NotBlank
    @Size(min = 3, max = 30)
    @Pattern(regexp = "^[\\p{L}\\p{M}0-9_-]+$")
    private String lastname;
    @Email
    @NotBlank
    @Size(max = 50)
    private String email;
    @NotBlank
    private String hashedSecret;
    @NotBlank
    @Pattern(regexp = "^\\+?[0-9]{9,15}$")
    private String phone;

}
