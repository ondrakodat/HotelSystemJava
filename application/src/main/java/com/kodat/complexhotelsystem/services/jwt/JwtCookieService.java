package com.kodat.complexhotelsystem.services.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class JwtCookieService {
    private static final int EXPIRATION_IN_MINUTES_DONT_REMEMBER_ME = 15;

    /**
     * Method for creating cookie with using our generated Jwt
     * We use builder for ResponseCookie
     * from creates the basics of accessToken is its name and token is value of our JWT, which we get from our generating Jwt class.
     * httpOnly means that JavaScript cant read value of this cookie. That lowers probability of stealing token with XSS, but the probability is not zero.
     * secure true means we can only send this Cookie throught Https with false we can use it also on Http but for production it's suggested to use it with true.
     * path is working for every way in our server
     * maxAge means how long can our cookie exist
     * sameSite "Lax" means when the browser send our cookie
     * @param token generated Jwt
     * @return cookie containing the JWT
     */
    public ResponseCookie createAccessCookieWithJwt(String token){

        return ResponseCookie
                .from("accessToken", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofMinutes(EXPIRATION_IN_MINUTES_DONT_REMEMBER_ME))
                .sameSite("Lax")
                .build();

    }

    public ResponseCookie createRefreshCookieWithJwt(String token, boolean rememberMe){
        Duration maxAge = rememberMe
                ? Duration.ofDays(15)
                : Duration.ofDays(1);

            return ResponseCookie
                    .from("refreshToken", token)
                    .httpOnly(true)
                    .secure(true)
                    .path("/api/auth/refresh")
                    .maxAge(maxAge)
                    .sameSite("Lax")
                    .build();
    }


    /**
     * Method for loading Jwt token from request. We get cookies from request to field.
     * If the field is not null because getCookies can return us null value if there aren't any cookies.
     * We go throught them all and try to find cookie with name accessToken, if we find it we return its value.
     * @param request
     * @return Optinal value if we find cookie with name "accessToken" we return its cookie value if we cant find cookie with this specification we return empty Optional
     */
    public Optional<String> loadJwtAccessFromCookie(HttpServletRequest request){
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

    public Optional<String> loadJwtRefreshFromCookie(HttpServletRequest request){
        Cookie [] cookies = request.getCookies();
        if(cookies != null){
            for (Cookie cookie : cookies){
                if(cookie.getName().equals("refreshToken")){
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Method for destroying cookie
     * @return
     */
    public ResponseCookie destroyAcessCookieWithLogOut(){
        return ResponseCookie
                .from("accessToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ZERO)
                .sameSite("Lax")
                .build();
    }

    public ResponseCookie destroyRefreshCookieWithLogOut() {
        return ResponseCookie
                .from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/refresh")
                .maxAge(Duration.ZERO)
                .sameSite("Lax")
                .build();
    }
}
