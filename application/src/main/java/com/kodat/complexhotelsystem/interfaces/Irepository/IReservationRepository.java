package com.kodat.complexhotelsystem.interfaces.Irepository;

import com.kodat.complexhotelsystem.entities.Reservation;

public interface IReservationRepository {
    Reservation createOrEditReservation(Reservation reservation);
    void deleteReservation(long idReservation);
}
