package com.kodat.complexhotelsystem.interfaces.Iservices.Imapper;

import com.kodat.complexhotelsystem.dtos.CreateReservationDto;
import com.kodat.complexhotelsystem.dtos.ReservationDto;
import com.kodat.complexhotelsystem.entities.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IReservationMapper {
    //@Mapping(source = "person.id", target = "personId")
    @Mapping(source = "room.id", target = "roomId")
    ReservationDto toReservationDto (Reservation reservation);
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "room", ignore = true)
    Reservation toEntity(ReservationDto reservationDto);
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "payed", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "checkIn", ignore = true)
    @Mapping(target = "planedCheckOut", ignore = true)
    @Mapping(target = "realCheckOut", ignore = true)
    Reservation toEntity(CreateReservationDto dto);
}
