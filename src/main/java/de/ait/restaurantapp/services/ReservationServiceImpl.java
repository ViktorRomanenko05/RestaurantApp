package de.ait.restaurantapp.services;

import de.ait.restaurantapp.dto.ReservationForm;
import de.ait.restaurantapp.enums.ReservationStatus;
import de.ait.restaurantapp.model.Reservation;
import de.ait.restaurantapp.model.RestaurantTable;


import de.ait.restaurantapp.repositories.ReservationRepo;
import de.ait.restaurantapp.repositories.RestaurantTableRepo;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import de.ait.restaurantapp.utility.ResIDGenerator;

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
    public Reservation createReservation(ReservationForm form) {
        LocalDateTime startDateTime = form.getStartDateTime();
        LocalDateTime endDateTime = startDateTime.plusHours(2);


        //choose all Tables >=Guest numb.

        List<RestaurantTable> availableTables = tableRepository.findAll().stream()
                .filter(t -> t.getCapacity() >= form.getGuestNumber())
                .sorted(Comparator.comparingInt(RestaurantTable::getCapacity))
                .toList();

        for (RestaurantTable table : availableTables) {
            boolean hasConflict = reservationRepos
                    .findByRestaurantTable_IdAndReservationStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
                            table.getId(), ReservationStatus.CONFIRMED, endDateTime, startDateTime
                    ).size() > 0;

            if (!hasConflict) {

                String reservationId = ReservationIdGenerator.generateReservationId();

                Reservation reservation = Reservation.builder()
                        .customerName(form.getCustomerName())
                        .customerEmail(form.getCustomerEmail())
                        .guestNumber(form.getGuestNumber())
                        .startDateTime(startDateTime)
                        .endDateTime(endDateTime)
                        .restaurantTable(table)
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

