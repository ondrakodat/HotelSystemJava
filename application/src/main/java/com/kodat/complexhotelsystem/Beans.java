package com.kodat.complexhotelsystem;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class Beans {
    @Bean
    public PasswordEncoder defineEncoder(){
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }
    @Bean
    public AuthenticationProvider defineProvider(UserDetailsService service, PasswordEncoder encoder){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(service);
        provider.setPasswordEncoder(encoder);
        return provider;
    }
    @Bean
    public SecurityFilterChain defineFilterChain(
            HttpSecurity http,
            @Qualifier("defineCorsConfiguration")
            CorsConfigurationSource corsConfig

    ) throws Exception{

        http.cors(cors -> cors.configurationSource(corsConfig))
                .csrf(csrf -> csrf.disable());



        http.authorizeHttpRequests(auth ->
                auth.requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                        .requestMatchers("/api/person/register").permitAll()
                        .anyRequest().authenticated());

        return http.build();
    }
    @Bean
    public AuthenticationManager defineManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }
    @Bean
    public CorsConfigurationSource defineCorsConfiguration(){
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:5173"));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }

}
