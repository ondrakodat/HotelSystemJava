package com.kodat.complexhotelsystem.controllers;

import com.kodat.complexhotelsystem.dtos.AuthDto;
import com.kodat.complexhotelsystem.services.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    //TODO add jwt token after succesfull auth
    @PostMapping("/login")
    public void authenticate(
           @RequestBody AuthDto dto){
        authService.authenticate(dto);
        System.out.printf("");
    }
}
