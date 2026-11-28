package com.kodat.complexhotelsystem.dtos;

import com.kodat.complexhotelsystem.enums.RoomType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateReservationDto {

    private RoomType roomType;

    private LocalDate startOfReservation;
    private LocalDate endOfReservation;

    private int members;

    private boolean earlyCheckIn;
    private boolean lateCheckOut;
}
