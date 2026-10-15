package com.kodat.complexhotelsystem.interfaces.Iservices;

import com.kodat.complexhotelsystem.dtos.PersonDto;
import com.kodat.complexhotelsystem.entities.Person;

public interface IPersonService {
    void createPersonDto(PersonDto person);
}
