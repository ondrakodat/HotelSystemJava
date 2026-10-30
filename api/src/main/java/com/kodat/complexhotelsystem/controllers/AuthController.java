package com.kodat.complexhotelsystem.controllers;

import com.kodat.complexhotelsystem.dtos.AuthDto;
import com.kodat.complexhotelsystem.dtos.RegisterDto;
import com.kodat.complexhotelsystem.dtos.passwordResetDtos.PasswordResetConfirmDto;
import com.kodat.complexhotelsystem.dtos.passwordResetDtos.PasswordResetRequestDto;
import com.kodat.complexhotelsystem.interfaces.Iservices.IPersonService;
import com.kodat.complexhotelsystem.interfaces.Iservices.IResetPasswordService;
import com.kodat.complexhotelsystem.services.AuthService;
import com.kodat.complexhotelsystem.services.jwt.JwtCookieService;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtCookieService cookieService;
    private final IPersonService personService;
    private final IResetPasswordService passwordResetService;

    @PostMapping("/register")
    @CrossOrigin
    public void createPerson(
            @RequestBody RegisterDto dto){
        personService.createPersonDto(dto);
        System.out.println("Dotaz došel na controller");
    }

    /**
     * End point for authenticating user we get AuthDto which contains email and secret
     * Go through authenticate method which returns us Jwt and this method we use as parameter for createCookieWithJwt
     * After that we return ResponseEntity with cookie
     * @param dto
     * @return HTTP 200 ok  with the Set-Cookie header
     */
    @PostMapping("/login")
    public ResponseEntity<Void> authenticate(
           @RequestBody AuthDto dto){
        ResponseCookie cookie = cookieService.createCookieWithJwt(authService.authenticate(dto));

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken csrfToken) {
        return csrfToken;
    }

    @PostMapping("/resetPassword/requestForReset")
    public ResponseEntity<Void> resetPasswordRequest(
            @RequestBody PasswordResetRequestDto dto
    ){
        String token = passwordResetService.saveTokenToDbAndReturnCodeForUser(dto.email());
        System.out.println("Reset token : " + token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resetPassword/confirmReset")
    public ResponseEntity<Void> confirmResetPassword(
            @RequestBody PasswordResetConfirmDto dto
            ){
        passwordResetService.setNewPasswordForPerson(dto.code(), dto.email(), dto.password());
        return ResponseEntity.ok().build();
    }

}
