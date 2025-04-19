package de.ait.restaurantapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailDto {
    private String to;
    private Long reservationId;
    private String name;

    private final String subject = "Подтверждение брони";

    public EmailDto(String to, Long restaurantId, String name) {
        this.to = to;
        this.reservationId = restaurantId;
        this.name = name;
    }
}
