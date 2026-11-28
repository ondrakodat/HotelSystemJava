    package com.kodat.complexhotelsystem.dtos;

    import com.kodat.complexhotelsystem.enums.ReservationPayStatus;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;

    import java.math.BigDecimal;
    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.util.Date;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public class ReservationDto {
        private Long id;
        private Long roomId;

        private LocalDate startOfReservation;
        private LocalDate endOfReservation;

        private ReservationPayStatus paymentStatus;
        private BigDecimal totalPrice;

        private int members;

        private LocalDateTime checkIn;
        private LocalDateTime plannedCheckOut;
        private LocalDateTime realCheckOut;

        private boolean earlyCheckIn;
        private boolean lateCheckOut;

    }
