package com.kodat.complexhotelsystem.services.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;

import javax.crypto.SecretKey;
import java.util.Base64;

public class JwtService {
    private final SecretKey secretKey;
    private final long expirationTime = 15*60*1000;
    private final String jwtSecret = "405B389E157UJ1546A";

    public JwtService(){
        this.secretKey = getSecretKey();
    }

    private SecretKey getSecretKey(){
        byte[] bytes = Decoders.BASE64.decode(this.jwtSecret);
        return Keys.hmacShaKeyFor(bytes);
    }

}
