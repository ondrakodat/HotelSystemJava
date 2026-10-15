package com.kodat.complexhotelsystem.interfaces.Iservices.Imapper;

import com.kodat.complexhotelsystem.dtos.RegisterDto;
import com.kodat.complexhotelsystem.entities.Person;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IRegisterMapper {
    Person toPerson(RegisterDto dto);
    RegisterDto toDto (Person person);
}
