package com.kodat.complexhotelsystem.controllers;

import com.kodat.complexhotelsystem.beans.SecurityBean;
import com.kodat.complexhotelsystem.dtos.RefreshTokenRotationResultDto;
import com.kodat.complexhotelsystem.dtos.TokenPairDto;
import com.kodat.complexhotelsystem.interfaces.IEmail.IEmailSender;
import com.kodat.complexhotelsystem.interfaces.Iservices.IResetPasswordService;
import com.kodat.complexhotelsystem.services.AuthService;
import com.kodat.complexhotelsystem.services.PersonService;
import com.kodat.complexhotelsystem.services.RateLimitService;
import com.kodat.complexhotelsystem.services.jwt.AuthTokenFiltr;
import com.kodat.complexhotelsystem.services.jwt.JwtAcessTokenGeneration;
import com.kodat.complexhotelsystem.services.jwt.JwtCookieService;
import com.kodat.complexhotelsystem.services.jwt.JwtRefreshTokenGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import jakarta.servlet.http.Cookie;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc
@Import({SecurityBean.class, JwtCookieService.class, AuthControllerLogoutTest.TestBeans.class})
class AuthControllerLogoutTest {
    @Autowired MockMvc mockMvc;

    @MockitoBean AuthService authService;
    @MockitoBean IResetPasswordService passwordResetService;
    @MockitoBean IEmailSender emailSender;
    @MockitoBean JwtRefreshTokenGeneration refreshTokenGenerator;
    @MockitoBean JwtAcessTokenGeneration accessTokenGenerator;
    @MockitoBean PersonService filterPersonService;
    @MockitoBean RateLimitService rateLimitService;

    @BeforeEach
    void allowRequestThroughTheProductionRateLimitInterceptor() {
        when(rateLimitService.checkLimit(anyString(), anyInt(), anyLong()))
                .thenReturn(new RateLimitService.Decision(true, 0));
    }

    @Test
    void logoutRevokesPresentedRefreshTokenAndExpiresBothCookies() throws Exception {
        var response = mockMvc.perform(post("/api/auth/logout")
                        .with(csrf())
                        .cookie(new Cookie("refreshToken", "presented-token")))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        verify(refreshTokenGenerator).revokeRefreshToken("presented-token");
        List<String> setCookies = response.getHeaders(HttpHeaders.SET_COOKIE);
        assertTrue(setCookies.stream().anyMatch(cookie ->
                cookie.contains("accessToken=") && cookie.contains("Path=/") && cookie.contains("Max-Age=0")));
        assertTrue(setCookies.stream().anyMatch(cookie ->
                cookie.contains("refreshToken=") && cookie.contains("Path=/api/auth") && cookie.contains("Max-Age=0")));
    }

    @Test
    void logoutWithoutRefreshCookieStillExpiresCookies() throws Exception {
        var response = mockMvc.perform(post("/api/auth/logout").with(csrf()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        verify(refreshTokenGenerator).revokeRefreshToken(null);
        assertTrue(response.getHeaders(HttpHeaders.SET_COOKIE).stream()
                .anyMatch(cookie -> cookie.contains("refreshToken=") && cookie.contains("Path=/api/auth")));
    }

    @Test
    void logoutStillRequiresCsrfToken() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isForbidden());
    }

    @Test
    void loginSetsSecureHttpOnlyCookiesAndDoesNotReturnTokensInBody() throws Exception {
        when(authService.authenticate(any())).thenReturn(new TokenPairDto("access-jwt", "refresh-token"));

        var response = mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"email\":\"guest@example.test\",\"password\":\"StrongPassword123!\",\"rememberMe\":true}"))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andReturn()
                .getResponse();

        List<String> setCookies = response.getHeaders(HttpHeaders.SET_COOKIE);
        assertTrue(setCookies.stream().anyMatch(cookie ->
                cookie.contains("accessToken=access-jwt") && cookie.contains("HttpOnly")
                        && cookie.contains("Secure") && cookie.contains("Path=/")));
        assertTrue(setCookies.stream().anyMatch(cookie ->
                cookie.contains("refreshToken=refresh-token") && cookie.contains("HttpOnly")
                        && cookie.contains("Secure") && cookie.contains("Path=/api/auth")));
        verify(authService).authenticate(any());
    }

    @Test
    void csrfEndpointReturnsToken() throws Exception {
        var response = mockMvc.perform(get("/api/auth/csrf"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertTrue(response.getContentAsString().contains("\"token\":\""));
    }

    @Test
    void registrationCreatesPersonAndSendsWelcomeEmail() throws Exception {
        when(emailSender.renderEmail(eq("emails/welcome"), anyMap())).thenReturn("welcome email");

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"firstname\":\"Alex\",\"lastname\":\"Test\",\"email\":\"guest@example.test\",\"hashedSecret\":\"StrongPassword123!\",\"phone\":\"+420123456789\"}"))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Pokud email existuje, odešleme Vám další informace k registraci"));

        verify(filterPersonService).createPersonDto(any());
        verify(emailSender).sendHtmlEmail("Vítejte!", "guest@example.test", "welcome email");
    }

    @Test
    void refreshWithoutCookieReturnsUnauthorizedAndClearsCookies() throws Exception {
        var response = mockMvc.perform(post("/api/auth/refresh").with(csrf()))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        List<String> setCookies = response.getHeaders(HttpHeaders.SET_COOKIE);
        assertTrue(setCookies.stream().anyMatch(cookie ->
                cookie.contains("accessToken=") && cookie.contains("Max-Age=0")));
        assertTrue(setCookies.stream().anyMatch(cookie ->
                cookie.contains("refreshToken=") && cookie.contains("Max-Age=0")));
        verifyNoInteractions(accessTokenGenerator);
        verify(refreshTokenGenerator, never()).refresh(anyString());
    }

    @Test
    void refreshRotatesTheRefreshCookieAndCreatesAnAccessCookie() throws Exception {
        when(refreshTokenGenerator.refresh("old-refresh-token")).thenReturn(
                new RefreshTokenRotationResultDto(
                        "new-refresh-token",
                        UUID.randomUUID(),
                        false,
                        "guest@example.test",
                        Instant.now().plusSeconds(300),
                        7L
                ));
        when(accessTokenGenerator.generateToken("guest@example.test", 7L)).thenReturn("new-access-token");

        var response = mockMvc.perform(post("/api/auth/refresh")
                        .with(csrf())
                        .cookie(new Cookie("refreshToken", "old-refresh-token")))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        List<String> setCookies = response.getHeaders(HttpHeaders.SET_COOKIE);
        assertTrue(setCookies.stream().anyMatch(cookie ->
                cookie.contains("accessToken=new-access-token") && cookie.contains("HttpOnly")
                        && cookie.contains("Secure")));
        assertTrue(setCookies.stream().anyMatch(cookie ->
                cookie.contains("refreshToken=new-refresh-token") && cookie.contains("Path=/api/auth")));
        verify(refreshTokenGenerator).refresh("old-refresh-token");
        verify(accessTokenGenerator).generateToken("guest@example.test", 7L);
    }

    @Test
    void passwordResetRequestKeepsTheSameResponseWhenAccountDoesNotExist() throws Exception {
        when(passwordResetService.saveTokenToDbAndReturnCodeForUser("nobody@example.test"))
                .thenReturn(null);

        mockMvc.perform(post("/api/auth/resetPassword/requestForReset")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"email\":\"nobody@example.test\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Pokud email existuje zašleme Vám resetovací token"));

        verifyNoInteractions(emailSender);
    }

    @Test
    void passwordResetRequestSendsTheCodeWithoutReturningItToTheCaller() throws Exception {
        when(passwordResetService.saveTokenToDbAndReturnCodeForUser("guest@example.test"))
                .thenReturn("123456");
        when(emailSender.renderEmail(eq("emails/password_reset"), anyMap())).thenReturn("reset email");

        mockMvc.perform(post("/api/auth/resetPassword/requestForReset")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"email\":\"guest@example.test\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Pokud email existuje zašleme Vám resetovací token"));

        verify(emailSender).sendHtmlEmail("Obnovení hesla", "guest@example.test", "reset email");
    }

    @Test
    void passwordResetConfirmationRequiresCsrfAndDelegatesValidRequest() throws Exception {
        mockMvc.perform(post("/api/auth/resetPassword/confirmReset")
                        .contentType("application/json")
                        .content("{\"code\":\"123456\",\"email\":\"guest@example.test\",\"password\":\"StrongPassword123!\"}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/auth/resetPassword/confirmReset")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"code\":\"123456\",\"email\":\"guest@example.test\",\"password\":\"StrongPassword123!\"}"))
                .andExpect(status().isOk());

        verify(passwordResetService).setNewPasswordForPerson(
                "123456", "guest@example.test", "StrongPassword123!");
    }

    @TestConfiguration
    static class TestBeans {
        @Bean("defineCorsConfiguration")
        CorsConfigurationSource defineCorsConfiguration() {
            return request -> new CorsConfiguration();
        }

        @Bean
        AuthTokenFiltr authTokenFiltr(
                JwtAcessTokenGeneration jwt,
                PersonService personService,
                JwtCookieService cookies
        ) {
            return new AuthTokenFiltr(jwt, personService, cookies);
        }

        @Bean
        FilterRegistrationBean<AuthTokenFiltr> disableStandaloneFilterRegistration(AuthTokenFiltr filter) {
            FilterRegistrationBean<AuthTokenFiltr> registration = new FilterRegistrationBean<>(filter);
            registration.setEnabled(false);
            return registration;
        }
    }
}
