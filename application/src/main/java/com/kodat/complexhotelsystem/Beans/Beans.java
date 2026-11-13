package com.kodat.complexhotelsystem.Beans;

import com.kodat.complexhotelsystem.services.PersonService;
import com.kodat.complexhotelsystem.services.jwt.AuthTokenFiltr;
import com.kodat.complexhotelsystem.services.jwt.JwtCookieService;
import com.kodat.complexhotelsystem.services.jwt.JwtAcessTokenGeneration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class Beans {
    /**
        Defining Bean for PasswordEncoder interface and using its implementation Argon2 as our password encoder
     */
    @Bean
    public PasswordEncoder defineEncoder(){
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }
    /**
        Defining Bean Authentication provider to parameters we use UserDetailService and PasswordEncoder
        UserDetailService is used for searching user with email which is implemented in PersonService
        PasswordEncoder is used for encoding our password. We are using so we can be able to say if hash in our db is same as password hash what user gaved as input.
        So we basicaly just make DaoAuthProv obj. set password encoder and return it.
     */
    @Bean
    public AuthenticationProvider defineProvider(UserDetailsService service, PasswordEncoder encoder){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(service);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    /**
        Helping class AuthenticationConfiguration which provides us with AuthenticationManager, it's a class which use one of our used AuthProviders -> we are using
            DaoAuthprovider so manager will this one to do the work with authentication.
        We want to get from this method set up AuthenticationManager, we are trying to get it from AuthConfig where getAuthenticationManager() give us new one if there is not
            any already defined
        To ensure we don't have to make new every single time there are two fields to get that info for us.
            {@link AuthenticationManager} authenticationManager;
            and
            boolean authenticationManagerInitialized
        In first condition of our get method we try to get AuthManager which is already defined by our boolean check
        In second condition we use {@link AuthenticationManagerBuilder}, {@link org.springframework.context.ApplicationContext} where we are trying to find Bean for ManagerBuilder
            Application context also uses Bean Factory which we are using to find if there is already existing Bean for our object then we use atomic boolean to get and set the value on true
            Atomic boolean provides us the option of working with multi threads to be able to work with this type of boolean.
                If the value is set for true and got false we continue with our for statement.
                The point of it is we use {@link AuthenticationConfiguration} as midlle man so if someone already called our getMethod, and we are configuring new {@link AuthenticationManager}
                    we return the one which is already been created.
        In our for statement we go throught {@link GlobalAuthenticationConfigurerAdapter} which is applying configuration from each obj.
        Then we build {@link AuthenticationManager} from {@link AuthenticationManagerBuilder} by using it build method.
        Last if statement is trying to build it which I mentioned in previous sentence.

        In default configuration it returns {@link ProviderManager} which is implementation of this interface and also chooses correct provider to use it for auth.
     */
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

        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public AuthTokenFiltr defineAuthTokenFilter(
            JwtAcessTokenGeneration service,
            PersonService personService,
            JwtCookieService cookieService
    ){
        return new AuthTokenFiltr(service, personService, cookieService);
    }

}
