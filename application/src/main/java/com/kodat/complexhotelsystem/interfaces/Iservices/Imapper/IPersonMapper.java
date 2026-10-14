package com.kodat.complexhotelsystem.interfaces.Iservices.Imapper;

import com.kodat.complexhotelsystem.dtos.PersonDto;
import com.kodat.complexhotelsystem.entities.Person;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface IPersonMapper {
    PersonDto toDto(PersonDto person);
    Person toPerson(PersonDto dto);
}
