package com.kodat.complexhotelsystem.services;

import com.kodat.complexhotelsystem.dtos.ReservationDto;
import com.kodat.complexhotelsystem.entities.Reservation;
import com.kodat.complexhotelsystem.exceptions.reservation.InvalidReservationCreating;
import com.kodat.complexhotelsystem.interfaces.Irepository.IReservationRepository;
import com.kodat.complexhotelsystem.interfaces.Iservices.IReservationService;
import com.kodat.complexhotelsystem.interfaces.Iservices.Imapper.IReservationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService implements IReservationService {

    private final IReservationRepository repository;
    private final IReservationMapper mapper;

    @Override
    public ReservationDto createOrEditReservation(Reservation reservation) {
        //TODO
        if(reservation.getStartOfReservation().after(reservation.getEndOfReservation())){
            throw new InvalidReservationCreating("End of reservation can't be before start!");
        }
        return mapper.toDto(repository.createOrEditReservation(reservation));
    }

    @Override
    public void deleteReservation(long id) {
        repository.deleteReservation(id);
    }
}
