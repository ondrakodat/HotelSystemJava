package com.kodat.complexhotelsystem.Repository;

import com.kodat.complexhotelsystem.entities.Reservation;
import com.kodat.complexhotelsystem.interfaces.Irepository.IReservationRepository;
import com.kodat.complexhotelsystem.jpaIRepository.IReservationJpa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ReservationRepository implements IReservationRepository {
    private final IReservationJpa jpaRepository;

    @Override
    public Reservation createOrEditReservation(Reservation reservation) {
        return jpaRepository.save(reservation);
    }

    @Override
    public void deleteReservation(long idReservation) {
        jpaRepository.deleteById(idReservation);
    }
}
