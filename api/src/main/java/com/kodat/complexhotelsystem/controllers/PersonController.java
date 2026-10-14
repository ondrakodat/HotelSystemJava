package com.kodat.complexhotelsystem.controllers;

import com.kodat.complexhotelsystem.interfaces.Iservices.IPersonService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("api/person")
public class PersonController {
    private final IPersonService personService;
}
