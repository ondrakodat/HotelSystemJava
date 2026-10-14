package com.kodat.complexhotelsystem.dtos;

import com.kodat.complexhotelsystem.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class AuthDto {
    private String email;
    private String password;
}
