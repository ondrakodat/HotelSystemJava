package com.kodat.complexhotelsystem.entities;

import com.kodat.complexhotelsystem.enums.ReservationPayStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne()
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;
    @Column(nullable = false)
    private Date startOfReservation;
    @Column(nullable = false)
    private Date overOfReservation;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationPayStatus payed;
    @Column(nullable = false)
    private BigDecimal price;
    @Column(nullable = false)
    private int members;
    @Column(nullable = false)
    private Date checkIn;
    @Column(nullable = false)
    private Date planedCheckOut;
    private Date realCheckOut;
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
}
