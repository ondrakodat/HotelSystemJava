package com.kodat.complexhotelsystem.handlers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GoogleOAuth2FailureHandler implements AuthenticationFailureHandler {
    private final String failureRedirectAddress;

    public GoogleOAuth2FailureHandler(
            @Value("${app.frontend-url:http://localhost:5173}") String frontendAddress
    ) {
        if (frontendAddress == null || frontendAddress.isBlank()) {
            throw new IllegalArgumentException("Frontend address must not be blank");
        }

        String normalizedAddress = frontendAddress.endsWith("/")
                ? frontendAddress.substring(0, frontendAddress.length() - 1)
                : frontendAddress;
        this.failureRedirectAddress = normalizedAddress + "/login?googleError=true";
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {
        response.sendRedirect(failureRedirectAddress);
    }
}
