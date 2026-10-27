package com.kodat.complexhotelsystem.services.jwt;

import com.kodat.complexhotelsystem.entities.Person;
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

/**
 * {@link AuthTokenFiltr} is a class that represents filter of incoming http request, getting our Jwt from request, extracting email from it.
 * {@link OncePerRequestFilter} is abstract class that provides us with basics to create our filter for requests.
 * We use doFilterInternal which is method from {@link OncePerRequestFilter} class its method which framework calls while proceeding http request.
 */
public class AuthTokenFiltr extends OncePerRequestFilter {
    private final JwtTokenGeneration jwtService;
    private final PersonService personService;

    public AuthTokenFiltr(JwtTokenGeneration jwtService, PersonService personService) {
        this.jwtService = jwtService;
        this.personService = personService;
    }

    /**
     *
     * @param request represents actual request from frontend for example it can be POST, GET, PUT or DELETE with header of URL
     * @param response represents answer which server sends to client
     * @param filterChain represent set of other filters which we will give our request after we are done with it in this class.
     *  At first, we get header from our request by using getHeader(String "name") method. it may contain our Jwt token which we provided user when he logged in
     *  Then we go through if statement where we check if header is not null and starts with "Bearer " because after it there is our Jwt.
     *      also we check if there isn't context of authentication in SecurityContext if there is we don't configure it again.
     * We set up user with UserDetails by extracting email from Jwt and then searching for him in our database it returns {@link Person} and we can use it as UserDetails
     *                    because we extend our Person class with {@link UserDetails}
     * Then we create object {@link UsernamePasswordAuthenticationToken} because we use this object for authenticaion with email and password and also as object
     *                    representing existing authentizaed authenticaion.
     *                    Its constructor take 3 inputs - UserDetail service its object of user whose identity we want to represent.
     *                                                  - null because we don't need password again
     *                                                  - Authority of user for example Role.ADMIN;
*    * After this we add our authenticated user to SecurityContextHolder so we don't have to go throught this method every single time we send request
     * Last thing we are doing is sending request and response to other filters we will be using
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if(authHeader != null && authHeader.startsWith("Bearer ") && SecurityContextHolder.getContext().getAuthentication() == null){
            String token = authHeader.substring(7);
            String email = jwtService.extractEmailFromJwtToken(token);
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
