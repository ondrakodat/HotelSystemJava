package com.kodat.complexhotelsystem.controllers;

import com.kodat.complexhotelsystem.dtos.AuthDto;
import com.kodat.complexhotelsystem.dtos.AuthResponseDto;
import com.kodat.complexhotelsystem.services.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponseDto authenticate(
           @RequestBody AuthDto dto){
        System.out.println("Dotaz dorazil na controller");
        return authService.authenticate(dto);
    }
}
