package com.kodat.complexhotelsystem.jpaIRepository;

import com.kodat.complexhotelsystem.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IReservationJpa extends JpaRepository<Reservation, Long> {
}
