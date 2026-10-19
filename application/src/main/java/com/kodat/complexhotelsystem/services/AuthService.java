package com.kodat.complexhotelsystem.services;

import com.kodat.complexhotelsystem.dtos.AuthDto;
import com.kodat.complexhotelsystem.dtos.AuthResponseDto;
import com.kodat.complexhotelsystem.services.jwt.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final AuthenticationManager manager;
    private final JwtService jwtService;

    public AuthResponseDto authenticate(AuthDto dto){
       manager.authenticate(new UsernamePasswordAuthenticationToken(
                dto.getEmail(),
                dto.getPassword()
        ));
        return new AuthResponseDto(jwtService.generateToken(dto.getEmail()));
    }

}
