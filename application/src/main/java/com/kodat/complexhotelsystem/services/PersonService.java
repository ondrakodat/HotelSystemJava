package com.kodat.complexhotelsystem.services;

import com.kodat.complexhotelsystem.dtos.PersonDto;
import com.kodat.complexhotelsystem.dtos.RegisterDto;
import com.kodat.complexhotelsystem.entities.Person;
import com.kodat.complexhotelsystem.exceptions.EmailAlreadyExistsException;
import com.kodat.complexhotelsystem.exceptions.InvalidPasswordException;
import com.kodat.complexhotelsystem.exceptions.PhoneAlreadyExistsException;
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

    @Override
    public void createPersonDto(RegisterDto dto) {
        if(personRepository.existsByEmail(dto.getEmail()))
            throw new EmailAlreadyExistsException("Email already exists!");
        if(!validatePersonPassword(dto.getHashedSecret()))
            throw new InvalidPasswordException("Password doesnt contains capital letters, numbers or special letter.");
        if(personRepository.existsByPhone(dto.getPhone()))
            throw new PhoneAlreadyExistsException("Mobile phone already exists");
        Person person = registerMapper.toPerson(dto);
        person.setSecret(encoder.encode(dto.getHashedSecret()));
        personRepository.createPerson(person);
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

    private boolean validatePersonPassword(String password){
        if(password == null)
            throw new InvalidPasswordException("Set password is null");

        if(password.length() < 12 || password.length() > 128)
            return false;

        return password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*[0-9].*") &&
                password.matches(".*[^a-zA-Z0-9].*");
    }

}
