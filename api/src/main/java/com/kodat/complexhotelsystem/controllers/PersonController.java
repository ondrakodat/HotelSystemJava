package com.kodat.complexhotelsystem.controllers;

import com.kodat.complexhotelsystem.dtos.PersonDto;
import com.kodat.complexhotelsystem.interfaces.Iservices.IUserService;
import com.kodat.complexhotelsystem.services.jwt.JwtCookieService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("api/person")
public class PersonController {
    private final IUserService userService;
    private final JwtCookieService cookieService;

    @GetMapping("/profile")
    public ResponseEntity<PersonDto> getUserProfile(
            Authentication auth
    ){
        return ResponseEntity.status(200).body(userService.findUserByEmail(auth.getName()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logoutPerson(){
        ResponseCookie cookie = cookieService.destroyAcessCookieWithLogOut();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }


}
