package de.ait.restaurantapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationFormDto {
    private String customerName;
    private String customerEmail;
    private int guestNumber;
    private LocalDateTime startDateTime;
    private LocalTime endTime;
}
