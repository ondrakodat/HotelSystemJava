package com.kodat.complexhotelsystem.services.jwt;

import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@AllArgsConstructor
@Service
public class JwtTokenGeneration {
    private final RsaKeysGeneration rsaKeys;

    final int expirationInMinutes = 15;
    final int expirationMinutesToSeconds = 60;
    final int expirationSecondsToMiliSeconds = 1000;
    final Date timeNow = new Date(System.currentTimeMillis());

    public String generateToken(String email){
        long expirationTimeOfToken = expirationInMinutes * expirationMinutesToSeconds * expirationSecondsToMiliSeconds;

        return Jwts
                .builder()
                .subject(email)
                .expiration(new Date(timeNow.getTime() + expirationTimeOfToken))
                .issuedAt(timeNow)
                .signWith(rsaKeys.getPrivateKey(), Jwts.SIG.RS256)
                .compact();
    }
}
