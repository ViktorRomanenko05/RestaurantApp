package de.ait.restaurantapp.services;

import de.ait.restaurantapp.dto.EmailDto;
import de.ait.restaurantapp.dto.ReservationFormDto;
import de.ait.restaurantapp.enums.ReservationStatus;
import de.ait.restaurantapp.model.Reservation;
import de.ait.restaurantapp.model.RestaurantTable;
import de.ait.restaurantapp.repositories.ReservationRepo;
import de.ait.restaurantapp.repositories.RestaurantTableRepo;
import de.ait.restaurantapp.utils.ReservationIDGenerator;
import jakarta.mail.MessagingException;
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
    private final EmailService emailService;

    @Override
    public Reservation createReservation(ReservationFormDto form) throws MessagingException {

        LocalDateTime startDateTime = form.getStartDateTime();
        LocalDateTime endDateTime = form.getEndDateTime();

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
                String reservationCode = ReservationIDGenerator.generateReservationId();

                Reservation reservation = Reservation.builder()
                        .reservationCode(reservationCode)
                        .customerName(form.getCustomerName())
                        .customerEmail(form.getCustomerEmail())
                        .guestCount(form.getGuestNumber())
                        .startDateTime(startDateTime)
                        .endDateTime(endDateTime)
                        .restaurantTable(table)
                        .reservationStatus(ReservationStatus.CONFIRMED)
                        // todo isAdmin() true or false
                        .build();
                // Инжектировал метод отправки Email
                EmailDto emailClientDto = new EmailDto();
                emailClientDto.setTo(form.getCustomerEmail());
                emailClientDto.setName(form.getCustomerName());
                emailClientDto.setReservationCode(reservationCode);

                emailService.sendHTMLEmail(emailClientDto);
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
    public void cancelReservation(String reservationCode) {
        Optional<Reservation> reservationOpt = reservationRepos.findByReservationCode(reservationCode);
        reservationOpt.ifPresent(reservation -> {
            reservation.setReservationStatus(ReservationStatus.CANCELED);
            reservationRepos.save(reservation);
        });
    }
}