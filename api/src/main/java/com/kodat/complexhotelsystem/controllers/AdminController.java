    package com.kodat.complexhotelsystem.controllers;

    import com.kodat.complexhotelsystem.dtos.PersonDto;
    import com.kodat.complexhotelsystem.interfaces.Iservices.IPersonService;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

    import java.util.List;

    @RestController
    @RequestMapping("/api/admin")
    public class AdminController {
        private final IPersonService personService;

        public AdminController(IPersonService personService) {
            this.personService = personService;
        }

        @GetMapping("/person/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public PersonDto getPerson(@PathVariable long id){
            return personService.findPersonDtoById(id);
        }

        @GetMapping("/person/getAll")
        @PreAuthorize("hasRole('ADMIN')")
        public List<PersonDto> getAllUsers(){
            return personService.getAllPersons();
        }



    }
