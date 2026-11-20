package com.kodat.complexhotelsystem.services;

import com.kodat.complexhotelsystem.dtos.GoogleUserDto;
import com.kodat.complexhotelsystem.entities.Person;
import com.kodat.complexhotelsystem.exceptions.GoogleEmailIsNotVerified;
import com.kodat.complexhotelsystem.interfaces.Irepository.IPersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.util.Random;

@AllArgsConstructor
public class GoogleOAuth2Service {
    private final IPersonRepository repository;
    private final PasswordEncoder encoder;


    public Person processGoogleUser(GoogleUserDto dto){
        Person personFoundByGoogleId = repository.findByGoogleId(dto.googleId());
        boolean personExistsByEmail = repository.existsByEmail(dto.email());

        if(personFoundByGoogleId != null){
            return personFoundByGoogleId;
        }

        if(personExistsByEmail){
            Person person = repository.findPersonByEmail(dto.email());
            if(!dto.emailVerified())
                throw new GoogleEmailIsNotVerified("Google email is not verified!");
            person.setGoogleId(dto.googleId());
            repository.editPerson(person);
            return person;
        } else {

            Random random = new SecureRandom();
            Person person = new Person();
            person.setGoogleId(dto.googleId());
            person.setLastname(dto.lastname());
            person.setFirstname(dto.firstname());
            person.setEmail(dto.email());
            person.setSecret(encoder.encode(generateRandomPassword()));
            return repository.createPerson(person);
        }
    }

    private String generateRandomPassword(){
        return null;
    }


}
