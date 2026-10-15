package com.kodat.complexhotelsystem.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterDto {
    private String username;
    private String lastname;
    private String email;
    private String hashedSecret;
    private String phone;

}
