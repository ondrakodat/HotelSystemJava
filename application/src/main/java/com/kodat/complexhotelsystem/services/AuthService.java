package com.kodat.complexhotelsystem.services;

import com.kodat.complexhotelsystem.dtos.AuthDto;
import com.kodat.complexhotelsystem.dtos.AuthResponseDto;
import com.kodat.complexhotelsystem.services.jwt.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final AuthenticationManager manager;
    private final JwtService jwtService;
    /*
        In our authenticate method we are using AuthenticationManager use bean of AuthenticationProvider  which we configured as Bean
        Manager automatically chooses DaoProvider as provider and use its hash for Argon2
        Method authenticate have argument Authentication, but we give UsernamePasswordAuthentication that class implements AbstractAuthenticationToken and that class implements
            our Authentication class
     */
    public AuthResponseDto authenticate(AuthDto dto){
        ProviderManager p = new ProviderManager();
       manager.authenticate(new UsernamePasswordAuthenticationToken(
                dto.getEmail(),
                dto.getPassword()
        ));
       /*
       After successful authentication with email and password we create JWT token for future authentication on end points
       */
        return new AuthResponseDto(jwtService.generateToken(dto.getEmail()));
    }

}
