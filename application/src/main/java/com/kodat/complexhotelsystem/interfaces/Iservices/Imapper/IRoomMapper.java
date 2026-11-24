package com.kodat.complexhotelsystem.interfaces.Iservices.Imapper;

import com.kodat.complexhotelsystem.dtos.RoomDto;
import com.kodat.complexhotelsystem.entities.Room;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        uses = ISpecialEquipmentMapper.class
)
public interface IRoomMapper {
    RoomDto toDto(Room room);
    Room toEntity(RoomDto dto);
}
