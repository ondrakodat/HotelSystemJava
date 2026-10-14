package com.kodat.complexhotelsystem.jpaIRepository;

import com.kodat.complexhotelsystem.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IPersonJpa extends JpaRepository<Person, Long> {
    Optional<Person> findPersonByEmail(String email);
}
