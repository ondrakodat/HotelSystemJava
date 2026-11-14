package com.kodat.complexhotelsystem.controllers;

import com.kodat.complexhotelsystem.Beans.SecurityBean;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
