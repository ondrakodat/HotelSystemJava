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

        if(reservation.getStartOfReservation().isAfter(reservation.getEndOfReservation())){
            throw new InvalidReservationCreating("End of reservation can't be before start!");

        }else if(reservation.getStartOfReservation().isBefore(LocalDate.now())){
            throw new InvalidReservationCreating("Start of reservation can't be in past");

        }else {

            for (Room r : roomRepository
                    .findRoomsByTypeAndCapacity(
                            reservation.getRoom().getType(),
                            reservation.getMembers(),
                            RoomState.AVAILABLE)) {
                if (reservationRepository
                        .checkIfThereIsNoTimeColidationWithReservation(
                                r.getId(),
                                reservation.getStartOfReservation(),
                                reservation.getEndOfReservation()
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


    @Override
    public void deleteReservation(long id) {
        reservationRepository.deleteReservation(id);
    }
}
