package de.ait.restaurantapp.dto;

import de.ait.restaurantapp.model.Reservation;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReservationDto {

    private Long id;
    private String customerName;
    private String customerEmail;
    private int guestNumber;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;


    public ReservationDto(Reservation reservation) {
        this.id = reservation.getId();
        this.customerName = reservation.getCustomerName();
        this.customerEmail = reservation.getCustomerEmail();

        this.guestNumber = reservation.getGuestNumber();
        this.startDateTime = reservation.getStartDateTime();
        this.endDateTime = reservation.getEndDateTime();
    }
}