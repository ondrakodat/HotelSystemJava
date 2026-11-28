package com.kodat.complexhotelsystem.controllers;

import com.kodat.complexhotelsystem.dtos.ReservationDto;
import com.kodat.complexhotelsystem.interfaces.Iservices.IReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
public class ReservationController {
    private final IReservationService reservationService;

    @PutMapping("/create")
    public ReservationDto createReservation(ReservationDto dto){
        reservationService.createOrEditReservation(dto);

        return null;
    }
}
