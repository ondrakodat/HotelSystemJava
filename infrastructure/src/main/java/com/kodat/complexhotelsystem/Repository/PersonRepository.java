package com.kodat.complexhotelsystem.Repository;

import com.kodat.complexhotelsystem.entities.Person;
import com.kodat.complexhotelsystem.interfaces.Irepository.IPersonRepository;
import com.kodat.complexhotelsystem.jpaIRepository.IPersonJpa;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class PersonRepository implements IPersonRepository {
    private final IPersonJpa jpaPerson;


    /**
     * Method for finding Person in database by email
     * @param email represents simply email by which we will try to find person from database
     * @return
     */
    @Override
    public Person findPersonByEmail(String email) {
        Optional<Person> person = jpaPerson.findPersonByEmail(email);
        if(person.isPresent())
            return person.get();
        else
            throw new RuntimeException("Person was not found");
    }

    /**
     * Method for creating Person in our database It's called from Service where we map Person to PersonDto
     * @param person represents user which will be added to database
     * @return
     */
    @Override
    public Person createPerson(Person person) {
        return jpaPerson.save(person);
    }
}
