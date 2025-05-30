package de.ait.restaurantapp.controller;

import de.ait.restaurantapp.dto.ReservationFormDto;
import de.ait.restaurantapp.exception.NoAvailableTableException;
import de.ait.restaurantapp.model.Reservation;
import de.ait.restaurantapp.services.FileService;
import de.ait.restaurantapp.services.ReservationService;
import jakarta.mail.MessagingException;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/restaurant")
public class RestaurantPageController {

    private final ReservationService reservationService;
    private final FileService fileService;

    public RestaurantPageController(ReservationService reservationService, FileService fileService) {
        this.reservationService = reservationService;
        this.fileService = fileService;
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
        boolean success = reservationService.cancelReservation(reservationCode);

        if (success) {
            model.addAttribute("message", "Reservation cancelled successfully!");
        } else {
            model.addAttribute("message", "Reservation Code is not found");
        }
        model.addAttribute("reservationCode", "");
        return "cancel-form";
    }

    @GetMapping("/menu")
    public ResponseEntity<byte[]> downloadMenu() {
        return fileService.getMenu()
                .map(file -> ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"")
                        .body(file.getData()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}


