package com.kodat.complexhotelsystem.controllers;

import com.kodat.complexhotelsystem.dtos.ReservationDto;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservation")
public class ReservationController {
    @PutMapping("/create")
    public ReservationDto createReservation(){


        return null;
    }
}
