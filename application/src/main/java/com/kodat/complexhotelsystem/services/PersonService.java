package com.kodat.complexhotelsystem.services;

import com.kodat.complexhotelsystem.dtos.PersonDto;
import com.kodat.complexhotelsystem.entities.Person;
import com.kodat.complexhotelsystem.interfaces.Irepository.IPersonRepository;
import com.kodat.complexhotelsystem.interfaces.Iservices.IPersonService;
import com.kodat.complexhotelsystem.interfaces.Iservices.Imapper.IPersonMapper;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PersonService implements IPersonService, UserDetailsService {
    private final IPersonRepository personRepository;
    private final IPersonMapper personMapper;

    @Override
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        return personRepository.findPersonByEmail(email);
    }

    @Override
    public void createPersonDto(PersonDto person) {
        personRepository.createPerson(personMapper.toPerson(person));
    }
}
