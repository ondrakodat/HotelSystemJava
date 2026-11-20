package com.kodat.complexhotelsystem.dtos;

public record GoogleUserDto (
    String googleId,
    String email,
    String firstname,
    String lastname,
    boolean emailVerified
){
}
