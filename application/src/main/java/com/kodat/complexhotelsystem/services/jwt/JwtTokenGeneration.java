package com.kodat.complexhotelsystem.services.jwt;

import io.jsonwebtoken.Claims;
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

    /**
     * Method for generating Token with input of email, we use this method after successful auth in our login end point.
     * We are using Jwts -> it's a class that provides us methods for creating JWT tokens, signing them, parsing them and authenticating them
     * builder -> return a new JwtsBuilder that can be configured we use this method when we are creating new Jwt token
     * subject -> tels us who this token belong who is identified by it
     * expiration -> obviously when this token will expire
     * issuedAt -> when the token was created
     * signWith -> by which authority was token signed
     * compact creates String from the configuration
     * @param email
     * @return String
     */
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

    public String extractEmailFromJwtToken(String token){

        return null;
    }

    /**
     *
     * @param token
     * @return
     */
    public Claims getClaims(String token){
        return Jwts.parser()
                .verifyWith(rsaKeys.getPublicKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
