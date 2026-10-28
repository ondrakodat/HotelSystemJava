package com.kodat.complexhotelsystem.services.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

@Service
public class JwtCookieService {
    private static final int EXPIRATION_IN_MINUTES = 15;

    /**
     * Method for creating cookie with using our generated Jwt
     * We use builder for ResponseCookie
     * from creates the basics of accessToken is its name and token is value of our JWT, which we get from our generating Jwt class.
     * httpOnly means that JavaScript cant read value of this cookie. That lowers probability of stealing token with XSS, but the probability is not zero.
     * secure true means we can only send this Cookie throught Https with false we can use it also on Http but for production its suggested to use it with true.
     * path is working for every way in our server
     * maxAge means how long can our cookie exist
     * sameSite "Lax" means when the browser send our cookie
     * @param token generated Jwt
     * @return cookie containing the JWT
     */
    public ResponseCookie createCookieWithJwt(String token){
        return ResponseCookie
                .from("accessToken", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofMinutes(EXPIRATION_IN_MINUTES))
                .sameSite("Lax")
                .build();

    }

    public Optional<String> loadJwtFromCookie(HttpServletRequest request){
        Cookie [] cookies = request.getCookies();
        if(cookies != null){
            for (Cookie cookie : cookies){
                if(cookie.getName().equals("accessToken")){
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }

    public ResponseCookie destroyCookieWithLogOut(){
        return null;
    }
}
