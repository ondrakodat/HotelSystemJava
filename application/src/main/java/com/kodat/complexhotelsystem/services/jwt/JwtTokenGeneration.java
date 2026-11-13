package com.kodat.complexhotelsystem.services.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

@AllArgsConstructor
@Service
public class JwtTokenGeneration {
    private final RsaKeysGeneration rsaKeys;

    private final static Duration ACCESS_TOKEN_LIFETIME = Duration.ofMinutes(15);

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

    public String generateToken(String email, boolean rememberMe){
        return null;
    }

    public String generateToken(String email){
        Date now = new Date();

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ACCESS_TOKEN_LIFETIME.toMillis()))
                .signWith(rsaKeys.getPrivateKey(), Jwts.SIG.RS256)
                .compact();
    }

    /**
     * Method for extracting email of user from token so we got Email as return value because in previous method -> generateToken we set email of
     *  authenticated user as subject.
     * @param token
     * @return
     */
    public String extractEmailFromJwtToken(String token){
        return getClaims(token).getSubject();
    }

    /**
     * {@link Claims} is interface that contains information from token for example expiration of the token or subject or even when the token was created
     * parser -> prepare method for processing our token (JwtParserBuilder)
     * verifyWith -> setup public key which authenticate our token signature
     * build -> crate prepared parser (JwtParser) and from it, we can parse our claims from token
     * parseSignedClaims -> get signed claims we check expiration and public key
     * getPayload -> we get the part of the token where are our important information
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
