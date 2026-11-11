package com.kodat.complexhotelsystem.controllers;

import com.kodat.complexhotelsystem.dtos.PersonDto;
import com.kodat.complexhotelsystem.interfaces.Iservices.IPersonService;
import com.kodat.complexhotelsystem.interfaces.Iservices.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("api/person")
public class PersonController {
    private final IUserService userService;

    @GetMapping("/profile")
    public ResponseEntity<PersonDto> getUserProfile(
            Authentication auth
    ){
        return ResponseEntity.status(200).body(userService.findUserByEmail(auth.getName()));
    }

    //TODO
    @PostMapping
    public ResponseEntity<Void> logoutPerson(){
        return null;
    }


}
