package com.kodat.complexhotelsystem.controllers;

import com.kodat.complexhotelsystem.dtos.PersonDto;
import com.kodat.complexhotelsystem.dtos.RegisterDto;
import com.kodat.complexhotelsystem.interfaces.Iservices.IPersonService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("api/person")
public class PersonController {
    private final IPersonService personService;

    @PostMapping("/register")
    @CrossOrigin
    public void createPerson(
           @RequestBody RegisterDto dto){
        personService.createPersonDto(dto);
        System.out.println("Dotaz došel na controller");
    }

    @GetMapping("/{id}")
    public PersonDto getPerson(@PathVariable long id){
        return personService.findPersonDtoById(id);
    }

    @GetMapping("/test")
    public String test(){
        return "You are logged in!";
    }
}
