package de.ait.restaurantapp.services;

import de.ait.restaurantapp.enums.ReservationStatus;

import de.ait.restaurantapp.model.Reservation;
import de.ait.restaurantapp.model.RestaurantTable;

import de.ait.restaurantapp.repositories.ReservationRepo;
import de.ait.restaurantapp.repositories.RestaurantTableRepo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepo reservationRepos;
    private final RestaurantTableRepo tableRepository;

    @Override
    public Reservation createReservation(String customerName, String customerEmail, int guestNumber,
                                         LocalDateTime startDateTime) {
        //+2H
        LocalDateTime endDateTime = startDateTime.plusHours(2);

        //choose all Tables >=Guest numb.
        List<RestaurantTable> availableTables = tableRepository.findAll().stream()
                .filter(t -> t.getCapacity() >= guestNumber)
                .sorted(Comparator.comparingInt(RestaurantTable::getCapacity)) // first<!!!
                .toList();

        //time/availability checking
        for (RestaurantTable table : availableTables) {
            boolean hasConflict = reservationRepos
                    .findByRestaurantTable_IdAndReservationStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
                            table.getId(), ReservationStatus.CONFIRMED, endDateTime, startDateTime
                    ).size() > 0;

            if (!hasConflict) {
                // reservation
                Reservation reservation = Reservation.builder()
                        .customerName(customerName)
                        .customerEmail(customerEmail)
                        .guestNumber(guestNumber)
                        .startDateTime(startDateTime)
                        .restaurantTable(table)
                        .endDateTime(endDateTime)
                        .reservationStatus(ReservationStatus.CONFIRMED)
                        .build();

                return reservationRepos.save(reservation);
            }
        }
        throw new RuntimeException("There are no tables available for the number of guests for this time.");
    }

    @Override
    public List<Reservation> getAllReservations() {
        return reservationRepos.findAll();
    }

    @Override
    public void cancelReservation(Long reservationId) {
        Optional<Reservation> reservationOpt = reservationRepos.findById(reservationId);
        reservationOpt.ifPresent(reservation -> {
            reservation.setReservationStatus(ReservationStatus.CANCELED);
            reservationRepos.save(reservation);
        });
    }
}