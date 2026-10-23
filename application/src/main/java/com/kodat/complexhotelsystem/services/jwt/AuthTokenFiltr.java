package com.kodat.complexhotelsystem.services.jwt;

import com.kodat.complexhotelsystem.services.PersonService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthTokenFiltr extends OncePerRequestFilter {
    private final JwtTokenGeneration jwtService;
    private final PersonService personService;

    public AuthTokenFiltr(JwtTokenGeneration jwtService, PersonService personService) {
        this.jwtService = jwtService;
        this.personService = personService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if(authHeader != null && authHeader.startsWith("Bearer ") && SecurityContextHolder.getContext().getAuthentication() == null){
            String token = authHeader.substring(7);
//            String email = jwtService.extractEmail(token);
            String email = "";
            UserDetails details = personService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken
                    (
                            details,
                            null,
                            details.getAuthorities()
                    );
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request, response);

    }
}
