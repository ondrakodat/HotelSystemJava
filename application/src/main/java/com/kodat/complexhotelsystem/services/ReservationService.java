package com.kodat.complexhotelsystem.services;

import com.kodat.complexhotelsystem.dtos.ReservationDto;
import com.kodat.complexhotelsystem.entities.Reservation;
import com.kodat.complexhotelsystem.entities.Room;
import com.kodat.complexhotelsystem.enums.RoomState;
import com.kodat.complexhotelsystem.exceptions.reservation.InvalidReservationCreating;
import com.kodat.complexhotelsystem.interfaces.Irepository.IReservationRepository;
import com.kodat.complexhotelsystem.interfaces.Irepository.IRoomRepository;
import com.kodat.complexhotelsystem.interfaces.Iservices.IReservationService;
import com.kodat.complexhotelsystem.interfaces.Iservices.Imapper.IReservationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class ReservationService implements IReservationService {

    private final IReservationRepository reservationRepository;
    private final IRoomRepository roomRepository;
    private final IReservationMapper mapper;

    @Override
    public ReservationDto createOrEditReservation(Reservation reservation) {
        //TODO

        if (!reservation.getStartOfReservation().isBefore(reservation.getEndOfReservation())){
            throw new InvalidReservationCreating("End of reservation can't be before start!");

        }else if(reservation.getStartOfReservation().isBefore(LocalDate.now())){
            throw new InvalidReservationCreating("Start of reservation can't be in past");

        }else {
            LocalDateTime plannedCheckIn = getPlannedCheckIn(reservation);
            LocalDateTime plannedCheckOut = getPlannedCheckOut(reservation);
            reservation.setCheckIn(plannedCheckIn);
            reservation.setPlanedCheckOut(plannedCheckOut);
            for (Room r : roomRepository
                    .findRoomsByTypeAndCapacity(
                            reservation.getRoom().getType(),
                            reservation.getMembers(),
                            RoomState.AVAILABLE)) {
                if (reservationRepository
                        .checkIfThereIsNoTimeColidationWithReservation(
                                r.getId(),
                                plannedCheckIn,
                                plannedCheckOut
                        )) {

                    reservation.setTotalPrice(buildTotalPrice(reservation, r.getPrice()));


                    reservation.setRoom(r);

                    return mapper.toDto(reservationRepository.createOrEditReservation(reservation));
                }
            }
            throw new InvalidReservationCreating("Can't find empty room for reservation try again later!");
        }
    }

    private float buildTotalPrice(Reservation reservation, float roomPrice){
        int dayCount = (int)ChronoUnit.DAYS.between(reservation.getStartOfReservation(), reservation.getEndOfReservation());
        float totalPrice = 0;

        totalPrice += roomPrice * dayCount;
        if(reservation.isEarlyCheckIn())
            totalPrice += reservation.getEarlyCheckInPrice();
        if(reservation.isLateCheckOut())
            totalPrice += reservation.getLateCheckouPrice();

        return totalPrice;
    }
    private LocalDateTime getPlannedCheckIn(Reservation reservation) {
        LocalTime checkInTime = reservation.isEarlyCheckIn()
                ? LocalTime.of(10, 0)
                : LocalTime.of(14, 0);

        return LocalDateTime.of(
                reservation.getStartOfReservation(),
                checkInTime
        );
    }

    private LocalDateTime getPlannedCheckOut(Reservation reservation) {
        LocalTime checkOutTime = reservation.isLateCheckOut()
                ? LocalTime.of(15, 0)
                : LocalTime.of(11, 0);

        return LocalDateTime.of(
                reservation.getEndOfReservation(),
                checkOutTime
        );
    }


    @Override
    public void deleteReservation(long id) {
        reservationRepository.deleteReservation(id);
    }
}
