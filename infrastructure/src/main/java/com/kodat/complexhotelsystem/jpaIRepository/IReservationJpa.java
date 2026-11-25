package com.kodat.complexhotelsystem.jpaIRepository;

import com.kodat.complexhotelsystem.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface IReservationJpa extends JpaRepository<Reservation, Long> {

    boolean existsByRoomIdAndStartOfReservationBeforeAndEndOfReservationAfter(
            Long roomId,
            LocalDate newEnd,
            LocalDate newStart
    );

}
