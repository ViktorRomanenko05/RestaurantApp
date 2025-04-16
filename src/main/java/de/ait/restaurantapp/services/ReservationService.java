package de.ait.restaurantapp.services;
import de.ait.restaurantapp.model.Reservation;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationService {

    Reservation createReservation(String customerName, String customerEmail, int guestNumber,

                                  LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<Reservation> getAllReservations();

    void cancelReservation(Long reservationId);
}
