package com.kodat.complexhotelsystem.Repository;

import com.kodat.complexhotelsystem.entities.Person;
import com.kodat.complexhotelsystem.interfaces.Irepository.IPersonRepository;
import com.kodat.complexhotelsystem.jpaIRepository.IPersonJpa;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class PersonRepository implements IPersonRepository {
    private final IPersonJpa jpaPerson;


    @Override
    public Person findPersonByEmail(String email) {
        Optional<Person> person = jpaPerson.findPersonByEmail(email);
        if(person.isPresent())
            return person.get();
        else
            throw new RuntimeException("Person was not found");
    }
}
