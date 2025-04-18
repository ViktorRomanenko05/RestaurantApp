package de.ait.restaurantapp.controllers;

import de.ait.restaurantapp.dto.ReservationForm;
import de.ait.restaurantapp.dto.ReservationDto;
import de.ait.restaurantapp.model.Reservation;
import de.ait.restaurantapp.services.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor

public class ResController {
    private final ReservationService reservationService;

    // creation
    @PostMapping
    public ResponseEntity<ReservationDto> createReservation(@RequestBody ReservationForm form) {
        Reservation reservation = reservationService.createReservation(form);
        ReservationDto reservationDto = new ReservationDto(reservation); // Zmieniamy na DTO
        return ResponseEntity.ok(reservationDto);
    }

    // gathering all reservations
    @GetMapping
    public List<ReservationDto> getAllReservations() {
        return reservationService.getAllReservations().stream()
                .map(ReservationDto::new) // Przekształcamy w DTO
                .toList();
    }

    // cancelation
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long reservationId) {
        reservationService.cancelReservation(reservationId);
        return ResponseEntity.noContent().build();
    }
}
