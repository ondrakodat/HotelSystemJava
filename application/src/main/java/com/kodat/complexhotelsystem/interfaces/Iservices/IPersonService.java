package com.kodat.complexhotelsystem.interfaces.Iservices;

import com.kodat.complexhotelsystem.dtos.PersonDto;
import com.kodat.complexhotelsystem.dtos.RegisterDto;
import com.kodat.complexhotelsystem.entities.Person;

public interface IPersonService {
    void createPersonDto(RegisterDto person);
}
