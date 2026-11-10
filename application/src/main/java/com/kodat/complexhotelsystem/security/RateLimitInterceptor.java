package com.kodat.complexhotelsystem.security;

import com.kodat.complexhotelsystem.services.RateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@AllArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitService limitService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ipAddress = request.getRemoteAddr();
        String endPoint = request.getRequestURI();
        String key = ipAddress + ":" + endPoint;

        RateLimitService.Decision decision =
                limitService.checkLimit(key, 5, 300_000);

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
