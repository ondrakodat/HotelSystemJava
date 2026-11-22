package com.kodat.complexhotelsystem.handlers;

import com.kodat.complexhotelsystem.entities.Person;
import com.kodat.complexhotelsystem.services.GoogleOAuth2Service;
import com.kodat.complexhotelsystem.services.jwt.JwtAcessTokenGeneration;
import com.kodat.complexhotelsystem.services.jwt.JwtCookieService;
import com.kodat.complexhotelsystem.services.jwt.JwtRefreshTokenGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoogleOAuth2HandlersTest {
    @Mock GoogleOAuth2Service googleOAuth2Service;
    @Mock JwtAcessTokenGeneration accessTokenGeneration;
    @Mock JwtRefreshTokenGeneration refreshTokenGeneration;
    @Mock Authentication authentication;
    @Mock OidcUser oidcUser;

    @Test
    void successCreatesJwtCookiesAndRedirectsToConfiguredFrontend() throws Exception {
        Person person = new Person();
        person.setEmail("guest@example.test");
        person.setTokenVersion(3L);

        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getSubject()).thenReturn("google-subject-123");
        when(oidcUser.getEmail()).thenReturn("guest@example.test");
        when(oidcUser.getGivenName()).thenReturn("Alex");
        when(oidcUser.getFamilyName()).thenReturn("Tester");
        when(oidcUser.getEmailVerified()).thenReturn(true);
        when(googleOAuth2Service.processGoogleUser(argThat(dto ->
                dto.googleId().equals("google-subject-123")
                        && dto.email().equals("guest@example.test")
                        && dto.emailVerified()
        ))).thenReturn(person);
        when(accessTokenGeneration.generateToken("guest@example.test", 3L))
                .thenReturn("access-jwt");
        when(refreshTokenGeneration.createRefreshToken(person, false))
                .thenReturn("refresh-token");

        GoogleOAuth2SuccessHandler handler = new GoogleOAuth2SuccessHandler(
                googleOAuth2Service,
                accessTokenGeneration,
                refreshTokenGeneration,
                new JwtCookieService(),
                "https://hotel.example.test"
        );
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession oauthSession = (MockHttpSession) request.getSession(true);
        MockHttpServletResponse response = new MockHttpServletResponse();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        handler.onAuthenticationSuccess(
                request,
                response,
                authentication
        );

        assertEquals("https://hotel.example.test/", response.getRedirectedUrl());
        List<String> cookies = response.getHeaders(HttpHeaders.SET_COOKIE);
        assertTrue(cookies.stream().anyMatch(cookie ->
                cookie.contains("accessToken=access-jwt")
                        && cookie.contains("HttpOnly")
                        && cookie.contains("Secure")
                        && cookie.contains("Path=/")));
        assertTrue(cookies.stream().anyMatch(cookie ->
                cookie.contains("refreshToken=refresh-token")
                        && cookie.contains("HttpOnly")
                        && cookie.contains("Secure")
                        && cookie.contains("Path=/api/auth")));
        assertTrue(oauthSession.isInvalid());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(refreshTokenGeneration).createRefreshToken(person, false);
    }

    @Test
    void failureRedirectsWithoutExposingExceptionDetails() throws Exception {
        GoogleOAuth2FailureHandler handler =
                new GoogleOAuth2FailureHandler("https://hotel.example.test/");
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationFailure(
                new MockHttpServletRequest(),
                response,
                new AuthenticationServiceException("sensitive provider error")
        );

        assertEquals(
                "https://hotel.example.test/login?googleError=true",
                response.getRedirectedUrl()
        );
    }
}
