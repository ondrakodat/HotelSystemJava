package com.kodat.complexhotelsystem.services.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey secretKey;
    private final long expirationTime = 15*60*1000;
    private final String jwtSecret = "405B389E157UJ1546A";

    public JwtService() {
        this.secretKey = getSigningKey();
    }


    SecretKey getSigningKey(){
        return Jwts.SIG.HS256.key().build();
    }

    public String generateToken(String email){
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }

    public String extractEmail(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(jwtSecret)
                .getPayload()
                .getSubject();
    }

}
