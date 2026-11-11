package com.kodat.complexhotelsystem.Beans;

import com.kodat.complexhotelsystem.services.jwt.AuthTokenFiltr;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityBean {
    /**
     * Defining filter for incoming HTTP requests, it configures CORS, CSRF, access rules and position of our custom Jwt filter
     * @param http
     * @param corsConfig provides us CorsConfiguration used by Spring Security
     * @param authTokenFiltr our Custom filter responsible for processing Jwt
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain defineFilterChain(
            HttpSecurity http,
            @Qualifier("defineCorsConfiguration")
            CorsConfigurationSource corsConfig,
            AuthTokenFiltr authTokenFiltr
    ) throws Exception{

        /*
         * Configure of cors using its provided configuration source
         * This controls which cross-origin request are permited by the browsers CORS mechanism.
         */
        http.cors(cors -> cors.configurationSource(corsConfig))
                /*
                    Disable CSRF protection for this security filter chain.
                    This can be appropriate for a stateless API using bearer tokens that are not automaticaly attached by the browser
                 */
                .csrf(csrf -> csrf.spa());

        /*
            Define which requests need an authentication and which requests require authenticated user
         */
        http.authorizeHttpRequests(auth ->
                //Alowing all requests to those end points
                auth.requestMatchers("/api/auth/login", "/api/auth/csrf", "/api/auth/register", "/api/auth/resetPassword/**").permitAll()
                        .requestMatchers("/api/person/test").hasRole("USER")
                        // Requires auth for every other request
                        .anyRequest().authenticated());
        // Adding our
        http.addFilterBefore(authTokenFiltr, UsernamePasswordAuthenticationFilter.class);
        // Build HttpSercurity obj with builder
        return http.build();
    }
}
