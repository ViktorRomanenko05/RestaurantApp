package de.ait.restaurantapp.services;
import de.ait.restaurantapp.dto.ReservationFormDto;
import de.ait.restaurantapp.model.Reservation;

import java.util.List;

public interface ReservationService {

    Reservation createReservation(ReservationFormDto form);

    List<Reservation> getAllReservations();

    void cancelReservation(String reservationCode);
}
