package com.kodat.complexhotelsystem.interfaces.Iservices.Imapper;

import com.kodat.complexhotelsystem.dtos.ReservationDto;
import com.kodat.complexhotelsystem.entities.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IReservationMapper {
    //@Mapping(source = "person.id", target = "personId")
    @Mapping(source = "room.id", target = "roomId")
    ReservationDto toDto (Reservation reservation);
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "room", ignore = true)
    Reservation toEntity(ReservationDto reservationDto);
}
