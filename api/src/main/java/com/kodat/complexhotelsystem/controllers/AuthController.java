package com.kodat.complexhotelsystem.controllers;

import com.kodat.complexhotelsystem.dtos.AuthDto;
import com.kodat.complexhotelsystem.services.AuthService;
import com.kodat.complexhotelsystem.services.jwt.JwtCookieService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtCookieService cookieService;

    /**
     *
     * @param dto
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<Void> authenticate(
           @RequestBody AuthDto dto){
        ResponseCookie cookie = cookieService.createCookieWithJwt(authService.authenticate(dto));

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
}
