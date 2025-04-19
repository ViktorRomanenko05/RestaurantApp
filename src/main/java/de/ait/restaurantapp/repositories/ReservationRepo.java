package de.ait.restaurantapp.repositories;

//??? SO?
import de.ait.restaurantapp.model.Reservation;
import de.ait.restaurantapp.enums.ReservationStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// do we use List or ...DB???!!!
import java.time.LocalDateTime;
import java.util.List;
@Repository

public interface ReservationRepo extends JpaRepository<Reservation, Long> {

    //looking for reservation for exact  table, time overlapping
    List<Reservation> findByRestaurantTable_IdAndReservationStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
            Integer tableId,
            ReservationStatus status,
            LocalDateTime end,
            LocalDateTime start
    );
}
