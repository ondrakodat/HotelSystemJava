package com.kodat.complexhotelsystem.interfaces.Iservices.Imapper;

import com.kodat.complexhotelsystem.dtos.SpecialEquipmentDto;
import com.kodat.complexhotelsystem.entities.SpecialEquipment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ISpecialEquipmentMapper {
    SpecialEquipmentDto toDto(SpecialEquipment entity);
    SpecialEquipment toEntity(SpecialEquipmentDto dto);
}
