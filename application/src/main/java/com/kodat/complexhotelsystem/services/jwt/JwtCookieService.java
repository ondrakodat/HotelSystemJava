package com.kodat.complexhotelsystem.services.jwt;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JwtCookieService {
    public ResponseCookie createCookieWithJwt(String token){
        return null;
    }
    public Optional<String> loadJwtFromCookie(){
        return null;
    }
    public ResponseCookie destroyCookieWithLogOut(){
        return null;
    }
}
