    package com.kodat.complexhotelsystem.controllers;

    import com.kodat.complexhotelsystem.dtos.PersonDto;
    import com.kodat.complexhotelsystem.interfaces.Iservices.IPersonService;
    import com.kodat.complexhotelsystem.services.PersonService;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

    @RestController
    @RequestMapping("/api/admin")
    public class AdminController {
        private final IPersonService personService;

        public AdminController(IPersonService personService) {
            this.personService = personService;
        }

        @GetMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public PersonDto getPerson(@PathVariable long id){
            return personService.findPersonDtoById(id);
        }

    }
