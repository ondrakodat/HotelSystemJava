package com.kodat.complexhotelsystem.handlers;

import com.kodat.complexhotelsystem.dtos.GoogleUserDto;
import com.kodat.complexhotelsystem.dtos.TokenPairDto;
import com.kodat.complexhotelsystem.entities.Person;
import com.kodat.complexhotelsystem.services.GoogleOAuth2Service;
import com.kodat.complexhotelsystem.services.jwt.JwtAcessTokenGeneration;
import com.kodat.complexhotelsystem.services.jwt.JwtCookieService;
import com.kodat.complexhotelsystem.services.jwt.JwtRefreshTokenGeneration;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@AllArgsConstructor
public class GoogleOAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final GoogleOAuth2Service googleOAuth2Service;
    private final JwtAcessTokenGeneration generatorJwt;
    private final JwtRefreshTokenGeneration refreshTokenGenerator;
    private final JwtCookieService cookieService;

    private final String frontendAddress = "http://localhost:5173/";

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OidcUser oidcUser =
                (OidcUser) authentication.getPrincipal();

        Person person = googleOAuth2Service
                .processGoogleUser(new GoogleUserDto(
                        oidcUser.getSubject(),
                        oidcUser.getEmail(),
                        oidcUser.getGivenName(),
                        oidcUser.getFamilyName(),
                        oidcUser.getEmailVerified()
                ));

        boolean rememberMe = false;
        TokenPairDto tokenPair = new TokenPairDto
                (
                        generatorJwt.generateToken(person.getEmail(), person.getTokenVersion()),
                        refreshTokenGenerator.createRefreshToken(person, rememberMe)
                );

        ResponseCookie accessCookie = cookieService.createAccessCookieWithJwt(tokenPair.accessToken());

        ResponseCookie refreshCookie = cookieService.createRefreshCookieWithJwt(tokenPair.refreshToken(), rememberMe);

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        response.sendRedirect(frontendAddress);

    }
}
