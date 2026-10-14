package com.kodat.complexhotelsystem.interfaces.Irepository;

import com.kodat.complexhotelsystem.entities.Person;

public interface IPersonRepository {
    Person findPersonByEmail(String email);
}
