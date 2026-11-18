package com.kodat.complexhotelsystem.services;

import com.kodat.complexhotelsystem.dtos.AuthDto;
import com.kodat.complexhotelsystem.dtos.TokenPairDto;
import com.kodat.complexhotelsystem.entities.Person;
import com.kodat.complexhotelsystem.exceptions.InvalidCredentialsException;
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

    public TokenPairDto authenticate(AuthDto dto){

        manager.authenticate(new UsernamePasswordAuthenticationToken(
                dto.getEmail(),
                dto.getPassword()
        ));

        Person person = repository.findPersonByEmail(dto.getEmail());
        if(person == null){
            throw new InvalidCredentialsException("Credentials doesn't match try again please");
        }
        TokenPairDto tokenPair = new TokenPairDto
                (
                        generatorJwt.generateToken(dto.getEmail(), person.getTokenVersion()),
                        refreshTokenGenerator.createRefreshToken(person, dto.isRememberMe())
                );
        return tokenPair;
    }



}
