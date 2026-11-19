package com.kodat.complexhotelsystem.Beans;

import com.kodat.complexhotelsystem.services.jwt.AuthTokenFiltr;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
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
                auth.requestMatchers(
                        "/api/auth/login",
                                "/api/auth/csrf",
                                "/api/auth/register",
                                "/api/auth/resetPassword/**",
                                "/api/auth/refresh",
                                "/api/auth/logout",
                                "/api/actuator/health"
                        ).permitAll()
                        .requestMatchers("/api/person/test").hasRole("USER")
                        // Requires auth for every other request
                        .anyRequest().authenticated())
                .oauth2Login(Customizer.withDefaults())
        ;

        // Adding our
        http.addFilterBefore(authTokenFiltr, UsernamePasswordAuthenticationFilter.class);
        // Build HttpSercurity obj with builder
        return http.build();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepostiory(){
        return new InMemoryClientRegistrationRepository(this.googleClientRegistration());
    }

    private ClientRegistration googleClientRegistration(){
        return ClientRegistration.withRegistrationId("google")
                .clientId("google-client-id")
                .clientSecret("google-client-secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope("openid", "profile", "email", "address", "phone")
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .tokenUri("https://www.googleapis.com/oauth2/v4/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName(IdTokenClaimNames.SUB)
                .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                .clientName("Google")
                .build();
    }
}
