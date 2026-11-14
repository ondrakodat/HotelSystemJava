package com.kodat.complexhotelsystem.services.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;

import static org.junit.jupiter.api.Assertions.*;

class JwtCookieServiceTest {
    private final JwtCookieService service = new JwtCookieService();

    @Test
    void refreshCookieIsScopedToAuthRoutesAndIsHttpOnlySecureAndSameSiteLax() {
        ResponseCookie cookie = service.createRefreshCookie("token", java.time.Duration.ofHours(1));

        assertEquals("refreshToken", cookie.getName());
        assertEquals("/api/auth", cookie.getPath());
        assertTrue(cookie.isHttpOnly());
        assertTrue(cookie.isSecure());
        assertEquals("Lax", cookie.getSameSite());
    }

    @Test
    void logoutCookiesExpireAtMatchingPaths() {
        ResponseCookie access = service.destroyAcessCookieWithLogOut();
        ResponseCookie refresh = service.destroyRefreshCookieWithLogOut();

        assertEquals("/", access.getPath());
        assertEquals(java.time.Duration.ZERO, access.getMaxAge());
        assertEquals("/api/auth", refresh.getPath());
        assertEquals(java.time.Duration.ZERO, refresh.getMaxAge());
        assertTrue(refresh.isHttpOnly());
        assertTrue(refresh.isSecure());
    }
}
