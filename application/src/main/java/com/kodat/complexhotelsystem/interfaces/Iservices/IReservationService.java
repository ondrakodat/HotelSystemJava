package com.kodat.complexhotelsystem.interfaces.Iservices;

import com.kodat.complexhotelsystem.dtos.CreateReservationDto;
import com.kodat.complexhotelsystem.dtos.ReservationDto;
import com.kodat.complexhotelsystem.entities.Reservation;

public interface IReservationService {
    ReservationDto createOrEditReservation(CreateReservationDto reservation);
    void deleteReservation(long id);
}
