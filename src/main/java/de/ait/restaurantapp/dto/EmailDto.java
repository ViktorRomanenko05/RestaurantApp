package de.ait.restaurantapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class EmailDto {
    private String to;
    private String reservationCode;
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int guestCount;

    private final String subject = "Подтверждение брони";

    public EmailDto(String to, String restaurantCode, String name,
                    LocalDateTime startTime, LocalDateTime endTime, int guestCount) {
        this.to = to;
        this.reservationCode = restaurantCode;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.guestCount = guestCount;
    }
}
