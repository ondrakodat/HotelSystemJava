package com.kodat.complexhotelsystem.services;

import com.kodat.complexhotelsystem.dtos.GoogleUserDto;
import com.kodat.complexhotelsystem.entities.Person;
import com.kodat.complexhotelsystem.exceptions.GoogleEmailIsNotVerified;
import com.kodat.complexhotelsystem.interfaces.Irepository.IPersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class GoogleOAuth2Service {
    private final IPersonRepository repository;
    private final PasswordEncoder encoder;

    public Person processGoogleUser(GoogleUserDto dto){
        if(!dto.emailVerified())
            throw new GoogleEmailIsNotVerified("Google email is not verified!");

        Person personFoundByGoogleId = repository.findByGoogleId(dto.googleId());


        if(personFoundByGoogleId != null){
            return personFoundByGoogleId;
        }
        boolean personExistsByEmail = repository.existsByEmail(dto.email());
        if(personExistsByEmail){
            Person person = repository.findPersonByEmail(dto.email());
            person.setGoogleId(dto.googleId());
            repository.editPerson(person);
            return person;
        } else {


            Person person = new Person();
            person.setGoogleId(dto.googleId());
            person.setLastname(dto.lastname());
            person.setFirstname(dto.firstname());
            person.setEmail(dto.email());
            person.setSecret(encoder.encode(generateRandomPassword()));
            return repository.createPerson(person);
        }
    }

    private String generateRandomPassword() {
        String SPECIAL_CHARS = "!@#$%^&*";
        String NUMBERS = "0123456789";
        String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";

        String ALL_CHARS = SPECIAL_CHARS + NUMBERS + UPPERCASE + LOWERCASE;
        SecureRandom random = new SecureRandom();
        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < 2; i++) {
            buffer.append(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));
            buffer.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
            buffer.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
            buffer.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        }
        for (int i = 0; i < 6; i++) {
            buffer.append(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }

        List<Character> characters = new ArrayList<>();
        for (char c : buffer.toString().toCharArray()) {
            characters.add(c);
        }

        Collections.shuffle(characters, random);
        StringBuilder result = new StringBuilder();
        for (char c : characters) {
            result.append(c);
        }

        return result.toString();
    }


}
