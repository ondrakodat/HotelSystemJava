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
        Person person = registerMapper.toPerson(dto);
        person.setHashedSecret(encoder.encode(dto.getHashedSecret()));
        personRepository.createPerson(person);
        System.out.println("Pozadavek dosel na service");
    }
}
