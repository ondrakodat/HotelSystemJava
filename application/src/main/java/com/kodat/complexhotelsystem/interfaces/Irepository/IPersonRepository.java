package com.kodat.complexhotelsystem.interfaces.Irepository;

import com.kodat.complexhotelsystem.entities.Person;

import java.util.List;
import java.util.Optional;

public interface IPersonRepository {
    Person findPersonByEmail(String email);
    Person createPerson(Person person);
    Person findPersonById(long id);
    Optional<List<Person>> findAllPersons();
}
