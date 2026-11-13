package com.kodat.complexhotelsystem.services;

import com.kodat.complexhotelsystem.dtos.AuthDto;
import com.kodat.complexhotelsystem.dtos.TokenPairDto;
import com.kodat.complexhotelsystem.entities.Person;
import com.kodat.complexhotelsystem.interfaces.Irepository.IPersonRepository;
import com.kodat.complexhotelsystem.services.jwt.JwtAcessTokenGeneration;
import com.kodat.complexhotelsystem.services.jwt.JwtRefreshTokenGeneration;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final AuthenticationManager manager;
    private final JwtAcessTokenGeneration generatorJwt;
    private final JwtRefreshTokenGeneration refreshTokenGenerator;
    private final IPersonRepository repository;
    /*
        In our authenticate method we are using AuthenticationManager use bean of AuthenticationProvider  which we configured as Bean
        Manager automatically chooses DaoProvider as provider and use its hash for Argon2
        Method authenticate have argument Authentication, but we give UsernamePasswordAuthentication that class implements AbstractAuthenticationToken and that class implements
            our Authentication class
     */
    // LEGACY CODE remaining for preventing errors while creating fix
    public String authenticate_(AuthDto dto){
       return generatorJwt.generateToken(dto.getEmail(), dto.isRememberMe());
    }

    public TokenPairDto authenticate(AuthDto dto){

        manager.authenticate(new UsernamePasswordAuthenticationToken(
                dto.getEmail(),
                dto.getPassword()
        ));

        Person person = repository.findPersonByEmail(dto.getEmail());
        TokenPairDto tokenPair = new TokenPairDto
                (
                        generatorJwt.generateToken(dto.getEmail()),
                        refreshTokenGenerator.createRefreshToken(person, dto.isRememberMe())
                );
        return tokenPair;
    }



}
