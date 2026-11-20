package com.kodat.complexhotelsystem.dtos;

import lombok.NoArgsConstructor;


public record GoogleUserDto (
    String googleId,
    String email,
    String firstname,
    String lastname,
    boolean emailVerified
){
}
