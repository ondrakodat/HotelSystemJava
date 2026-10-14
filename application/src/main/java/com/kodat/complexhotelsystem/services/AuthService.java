package com.kodat.complexhotelsystem.services;

import com.kodat.complexhotelsystem.dtos.AuthDto;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final AuthenticationManager manager;

    public void authenticate(AuthDto dto){
        manager.authenticate(new UsernamePasswordAuthenticationToken(
                dto.getEmail(),
                dto.getPassword()
        ));
    }

}
