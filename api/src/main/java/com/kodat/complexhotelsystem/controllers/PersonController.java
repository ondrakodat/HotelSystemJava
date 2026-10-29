package com.kodat.complexhotelsystem.controllers;

import com.kodat.complexhotelsystem.dtos.PersonDto;
import com.kodat.complexhotelsystem.dtos.RegisterDto;
import com.kodat.complexhotelsystem.interfaces.Iservices.IPersonService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("api/person")
public class PersonController {
    private final IPersonService personService;

    @GetMapping("/test")
    @PreAuthorize("hasRole('USER')")
    public String test(){
        return "You are logged in!";
    }
}
