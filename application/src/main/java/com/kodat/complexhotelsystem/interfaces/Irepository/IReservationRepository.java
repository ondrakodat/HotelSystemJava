package com.kodat.complexhotelsystem.interfaces.Irepository;

import com.kodat.complexhotelsystem.entities.Reservation;

import java.time.LocalDate;

public interface IReservationRepository {
    Reservation createOrEditReservation(Reservation reservation);
    void deleteReservation(long idReservation);
    boolean checkIfThereIsNoTimeColidationWithReservation(Long roomId, LocalDate newEnd, LocalDate newStart);
}
