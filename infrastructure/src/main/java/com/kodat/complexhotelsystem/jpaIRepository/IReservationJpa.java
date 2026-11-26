package com.kodat.complexhotelsystem.jpaIRepository;

import com.kodat.complexhotelsystem.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface IReservationJpa extends JpaRepository<Reservation, Long> {

    boolean existsByRoom_IdAndCheckInBeforeAndPlannedCheckOutAfter(
            Long roomId,
            LocalDateTime newCheckOut,
            LocalDateTime newCheckIn
    );


}
