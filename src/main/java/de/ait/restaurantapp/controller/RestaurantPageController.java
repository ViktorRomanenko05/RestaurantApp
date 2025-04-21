package de.ait.restaurantapp.controller;

import de.ait.restaurantapp.dto.ReservationFormDto;
import de.ait.restaurantapp.exception.NoAvailableTableException;
import de.ait.restaurantapp.model.Reservation;
import de.ait.restaurantapp.services.ReservationService;
import jakarta.mail.MessagingException;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/restaurant")
public class RestaurantPageController {

    private final ReservationService reservationService;

    public RestaurantPageController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public String showHomePage(Model model) {
        return "homepage";
    }

    @GetMapping("/reserve")
    public String showReservationPage(Model model) {
        model.addAttribute("reservationForm", new ReservationFormDto());
        return "reservation-form";
    }

    @PostMapping("/reserve")
    public String createReservation(@ModelAttribute ReservationFormDto form, Model model) {
        try {
            Reservation reservation = reservationService.createReservation(form);
            model.addAttribute("message", "Reservation created successfully!" +
                    " Your reservation code is: " + reservation.getReservationCode());
        } catch (MessagingException | IllegalArgumentException e) {
            model.addAttribute("message", "Error: " + e.getMessage());
            model.addAttribute("reservationForm", form);
        } catch (NoAvailableTableException exception) {
            model.addAttribute("message", exception.getMessage());
            model.addAttribute("reservationForm", form);
        }
        // reset the form
        model.addAttribute("reservationForm", new ReservationFormDto());
        return "reservation-form";
    }

    @GetMapping("/cancel")
    public String showCancelPage(Model model) {
        model.addAttribute("reservationCode", "");
        return "cancel-form";
    }

    @PostMapping("/cancel")
    public String cancelReservation(@RequestParam String reservationCode, Model model) {
        boolean cancelResult = reservationService.cancelReservation(reservationCode);

        if (cancelResult) {
            model.addAttribute("message", "Reservation cancelled successfully!");
        } else {
            model.addAttribute("message", "Reservation Code is not found");
        }
        model.addAttribute("reservationCode", "");
        return "cancel-form";
    }

    @GetMapping("/admin")
    public String showAdminPage() {
        return "admin-form";
    }
    /**
     * Возвращает все резервации на сегодняшний день для заданного столика.
     * Доступно только администратору.
     *
     * @param tableId ID столика
     * @return список резерваций
     */
    @GetMapping("/admin/{tableId}/today")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Reservation>> getReservationsForTableToday(
            @PathVariable Integer tableId
    ) {
        List<Reservation> reservations = reservationService.getReservationsForTableToday(tableId);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/admin/reservations/today")
    public String getAllReservationByDay(@RequestParam LocalDate day, Model model) {
        List<Reservation> reservations = reservationService.getAllReservationByDay(day);
        model.addAttribute("reservations", reservations);
        model.addAttribute("day", day);
        return "reservations";
    }

}


