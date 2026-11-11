package com.kodat.complexhotelsystem.services;

import com.kodat.complexhotelsystem.dtos.PersonDto;
import com.kodat.complexhotelsystem.entities.Person;
import com.kodat.complexhotelsystem.exceptions.PersonNotFoundException;
import com.kodat.complexhotelsystem.interfaces.Irepository.IPersonRepository;
import com.kodat.complexhotelsystem.interfaces.Iservices.IUserService;
import com.kodat.complexhotelsystem.interfaces.Iservices.Imapper.IPersonMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService implements IUserService {
    private final IPersonRepository repository;
    private final IPersonMapper mapper;

    @Override
    public PersonDto findUserByEmail(String email) {
        Person person = repository.findPersonByEmail(email);
        PersonDto dto;
        if(person != null){
            dto = mapper.toDto(person);
        }else{
            throw new PersonNotFoundException("User wasn't found by email");
        }
        return dto;
    }
}
