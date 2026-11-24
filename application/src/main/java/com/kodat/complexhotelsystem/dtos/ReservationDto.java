package com.kodat.complexhotelsystem.dtos;

import com.kodat.complexhotelsystem.enums.ReservationPayStatus;

import java.math.BigDecimal;
import java.util.Date;

public class ReservationDto {
    private Long id;
    private Long personId;
    private Long roomId;
    private Date startOfReservation;
    private Date overOfReservation;
    private ReservationPayStatus payed;
    private BigDecimal price;
    private int members;
    private Date checkIn;
    private Date planedCheckOut;
    private Date realCheckOut;
}
