package com.kodat.complexhotelsystem.services.jwt;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class JwtCookieService {
    private final int expirationInMinutes = 15;
    /**
     * Method for creating cookie with using our generated Jwt
     * We use builder for ResponseCookie
     * from creates the basics of accessToken is its name and token is value of our JWT, which we get from our generating Jwt class.
     * httpOnly means that JavaScript cant read value of this cookie. That lowers probability of stealing token with XSS, but the probability is not zero.
     * secure true means we can only send this Cookie throught Https with false we can use it also on Http but for production its suggested to use it with true.
     * path is working for every way in our server
     * maxAge means how long can our cookie exist
     * sameSite "Lax" means when the browser send our cookie
     * @param token
     * @return
     */
    public ResponseCookie createCookieWithJwt(String token){
        return ResponseCookie
                .from("accessToken", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofMinutes(expirationInMinutes))
                .sameSite("Lax")
                .build();

    }
    public Optional<String> loadJwtFromCookie(){
        return null;
    }
    public ResponseCookie destroyCookieWithLogOut(){
        return null;
    }
}
