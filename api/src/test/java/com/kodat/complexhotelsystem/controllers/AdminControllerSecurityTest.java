package com.kodat.complexhotelsystem.controllers;

import com.kodat.complexhotelsystem.Beans.SecurityBean;
import com.kodat.complexhotelsystem.dtos.PersonDto;
import com.kodat.complexhotelsystem.services.PersonService;
import com.kodat.complexhotelsystem.services.RateLimitService;
import com.kodat.complexhotelsystem.services.jwt.AuthTokenFiltr;
import com.kodat.complexhotelsystem.services.jwt.JwtAcessTokenGeneration;
import com.kodat.complexhotelsystem.services.jwt.JwtCookieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc
@Import({SecurityBean.class, JwtCookieService.class, AdminControllerSecurityTest.TestBeans.class})
class AdminControllerSecurityTest {
    @Autowired MockMvc mockMvc;

    @MockitoBean PersonService personService;
    @MockitoBean JwtAcessTokenGeneration jwtGenerator;
    @MockitoBean RateLimitService rateLimitService;

    @Test
    void anonymousCallerCannotReadAdminUserList() throws Exception {
        mockMvc.perform(get("/api/admin/person/getAll"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(personService);
    }

    @Test
    void ordinaryUserCannotReadAdminUserList() throws Exception {
        mockMvc.perform(get("/api/admin/person/getAll").with(user("guest").roles("USER")))
                .andExpect(status().isForbidden());

        verifyNoInteractions(personService);
    }

    @Test
    void adminCanReadUserList() throws Exception {
        when(personService.getAllPersons()).thenReturn(List.of(
                new PersonDto("Alex", "Test", "alex@example.test")
        ));

        mockMvc.perform(get("/api/admin/person/getAll").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("alex@example.test"));

        verify(personService).getAllPersons();
    }

    @Test
    void adminCanReadOneUser() throws Exception {
        when(personService.findPersonDtoById(42L))
                .thenReturn(new PersonDto("Alex", "Test", "alex@example.test"));

        mockMvc.perform(get("/api/admin/person/42").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alex@example.test"));

        verify(personService).findPersonDtoById(42L);
    }

    @TestConfiguration
    static class TestBeans {
        @Bean("defineCorsConfiguration")
        CorsConfigurationSource defineCorsConfiguration() {
            return request -> new CorsConfiguration();
        }

        @Bean
        AuthTokenFiltr authTokenFiltr(
                JwtAcessTokenGeneration jwt,
                PersonService personService,
                JwtCookieService cookies
        ) {
            return new AuthTokenFiltr(jwt, personService, cookies);
        }

        @Bean
        FilterRegistrationBean<AuthTokenFiltr> disableStandaloneFilterRegistration(AuthTokenFiltr filter) {
            FilterRegistrationBean<AuthTokenFiltr> registration = new FilterRegistrationBean<>(filter);
            registration.setEnabled(false);
            return registration;
        }
    }
}
