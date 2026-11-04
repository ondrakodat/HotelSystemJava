package com.kodat.complexhotelsystem.services;

import com.kodat.complexhotelsystem.dtos.PersonDto;
import com.kodat.complexhotelsystem.dtos.RegisterDto;
import com.kodat.complexhotelsystem.entities.Person;
import com.kodat.complexhotelsystem.interfaces.Irepository.IPersonRepository;
import com.kodat.complexhotelsystem.interfaces.Iservices.IPersonService;
import com.kodat.complexhotelsystem.interfaces.Iservices.Imapper.IPersonMapper;
import com.kodat.complexhotelsystem.interfaces.Iservices.Imapper.IRegisterMapper;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PersonService implements IPersonService, UserDetailsService {
    private final IPersonRepository personRepository;
    private final IPersonMapper personMapper;
    private final IRegisterMapper registerMapper;
    private final PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        return personRepository.findPersonByEmail(email);
    }
    // TODO Make method for ensure that password contains special symbols number and capital letter
    @Override
    public void createPersonDto(RegisterDto dto) {
        Person person = registerMapper.toPerson(dto);
        person.setSecret(encoder.encode(dto.getHashedSecret()));
        personRepository.createPerson(person);
        System.out.println("Pozadavek dosel na service");
    }

    @Override
    public PersonDto findPersonDtoById(long id) {
        Person person = personRepository.findPersonById(id);
        return personMapper.toDto(person);
    }

    @Override
    public List<PersonDto> getAllPersons() {
        List<Person> persons;
        List<PersonDto> dtosResult = new ArrayList<>();
        if(personRepository.findAllPersons().isPresent()) {
            persons = personRepository.findAllPersons().get();
        }
        else
            throw new RuntimeException("List of person is empty");

        for (Person personToDto : persons){
            dtosResult.add(personMapper.toDto(personToDto));
        }

        return dtosResult;
    }
}
