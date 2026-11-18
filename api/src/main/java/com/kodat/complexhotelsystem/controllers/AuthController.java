package com.kodat.complexhotelsystem.controllers;

import com.kodat.complexhotelsystem.dtos.AuthDto;
import com.kodat.complexhotelsystem.dtos.RefreshTokenRotationResultDto;
import com.kodat.complexhotelsystem.dtos.RegisterDto;
import com.kodat.complexhotelsystem.dtos.TokenPairDto;
import com.kodat.complexhotelsystem.dtos.passwordResetDtos.PasswordResetConfirmDto;
import com.kodat.complexhotelsystem.dtos.passwordResetDtos.PasswordResetRequestDto;
import com.kodat.complexhotelsystem.interfaces.IEmail.IEmailSender;
import com.kodat.complexhotelsystem.interfaces.Iservices.IPersonService;
import com.kodat.complexhotelsystem.interfaces.Iservices.IResetPasswordService;
import com.kodat.complexhotelsystem.services.AuthService;
import com.kodat.complexhotelsystem.services.jwt.JwtAcessTokenGeneration;
import com.kodat.complexhotelsystem.services.jwt.JwtCookieService;
import com.kodat.complexhotelsystem.services.jwt.JwtRefreshTokenGeneration;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtCookieService cookieService;
    private final IPersonService personService;
    private final IResetPasswordService passwordResetService;
    private final IEmailSender emailSender;
    private final JwtRefreshTokenGeneration refreshTokenGenerator;
    private final JwtAcessTokenGeneration accessTokenGenerator;

    @PostMapping("/register")
    @CrossOrigin
    public ResponseEntity<Void> createPerson(
           @Valid @RequestBody RegisterDto dto){

        personService.createPersonDto(dto);

        System.out.println("Dotaz došel na controller");
        String html = emailSender.renderEmail("emails/welcome", Map.of("firstname", dto.getFirstname()));
        emailSender.sendHtmlEmail("Vítejte!", dto.getEmail(), html);
        return ResponseEntity.status(HttpStatus.CREATED).build();
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
           @RequestBody @Valid AuthDto dto){

        TokenPairDto tokenPairDto = authService.authenticate(dto);

        ResponseCookie acessCookie = cookieService.createAccessCookieWithJwt(tokenPairDto.accessToken());

        ResponseCookie responseCookie = cookieService.createRefreshCookieWithJwt(tokenPairDto.refreshToken(), dto.isRememberMe());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, acessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .build();
    }

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken csrfToken) {
        return csrfToken;
    }

    @PostMapping("/resetPassword/requestForReset")
    public ResponseEntity<String> resetPasswordRequest(
            @RequestBody @Valid PasswordResetRequestDto dto
    ){
        String token = passwordResetService.saveTokenToDbAndReturnCodeForUser(dto.email());

       if(token != null){
           String html = emailSender.renderEmail("emails/password_reset", Map.of(
                   "resetCode", token
           ));
           emailSender.sendHtmlEmail("Obnovení hesla", dto.email(), html);
       }

        return ResponseEntity.ok().body("Pokud email existuje zašleme Vám resetovací token");
    }

    @PostMapping("/resetPassword/confirmReset")
    public ResponseEntity<Void> confirmResetPassword(
            @RequestBody @Valid PasswordResetConfirmDto dto
            ){
        System.out.println("END POINT FOR CONFIRM PASSWORD RESET CALLED");
        passwordResetService.setNewPasswordForPerson(dto.code(), dto.email(), dto.password());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(value = "refreshToken", required = false)
            String refreshToken
    ) {
        refreshTokenGenerator.revokeRefreshToken(refreshToken);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        cookieService.destroyAcessCookieWithLogOut().toString()
                )
                .header(
                        HttpHeaders.SET_COOKIE,
                        cookieService.destroyRefreshCookieWithLogOut().toString()
                )
                .build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshToken(
            @CookieValue(value = "refreshToken", required = false) String refreshToken
    ){
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header(HttpHeaders.SET_COOKIE,
                            cookieService.destroyAcessCookieWithLogOut().toString())
                    .header(HttpHeaders.SET_COOKIE,
                            cookieService.destroyRefreshCookieWithLogOut().toString())
                    .build();
        }

        try {
            RefreshTokenRotationResultDto result =
                    refreshTokenGenerator.refresh(refreshToken);

            String newAccessToken =
                    accessTokenGenerator.generateToken(result.email());

            ResponseCookie accessCookie =
                    cookieService.createAccessCookieWithJwt(newAccessToken);

            Duration remainingLifetime =
                    Duration.between(Instant.now(), result.expiresAt());

            if (remainingLifetime.isZero() || remainingLifetime.isNegative()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            ResponseCookie refreshCookie =
                    cookieService.createRefreshCookie(
                            result.refreshToken(),
                            remainingLifetime
                    );

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .build();

        } catch (IllegalArgumentException exception) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header(HttpHeaders.SET_COOKIE,
                            cookieService.destroyAcessCookieWithLogOut().toString())
                    .header(HttpHeaders.SET_COOKIE,
                            cookieService.destroyRefreshCookieWithLogOut().toString())
                    .build();
        }
    }

}
