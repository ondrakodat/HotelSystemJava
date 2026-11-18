package com.kodat.complexhotelsystem.security;

import com.kodat.complexhotelsystem.services.RateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

@Component
@AllArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitService limitService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ipAddress = request.getHeader("X-Verified-Client-IP");

        if (ipAddress == null
                || ipAddress.isBlank()
                || ipAddress.length() > 45
                || ipAddress.contains(",")) {
            ipAddress = request.getRemoteAddr();
        }

        ipAddress = ipAddress.trim();

        Object patternAttribute =
                request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

        String endpoint = patternAttribute instanceof String pattern
                ? pattern
                : "unmatched";

        String key = "rate-limit:auth:"
                + request.getMethod()
                + ":"
                + ipAddress
                + ":"
                + endpoint;

        RateLimitService.Decision decision;

        try {
            decision = limitService.checkLimit(key, 5, 300_000);
        } catch (DataAccessException exception) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.setHeader("Retry-After", "30");
            return false;
        }

        if (!decision.allowed()) {

            response.setStatus(429);

            response.setHeader(
                    "Retry-After",
                    String.valueOf(decision.retryAfterSeconds())
            );

        return false;
        }
        return true;
    }

}
