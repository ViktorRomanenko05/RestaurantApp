package de.ait.restaurantapp.controller;


import de.ait.restaurantapp.dto.ReservationFormDto;
import de.ait.restaurantapp.exception.NoAvailableTableException;
import de.ait.restaurantapp.model.Reservation;
import de.ait.restaurantapp.services.ReservationService;
import jakarta.mail.MessagingException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Controller
@RequestMapping("/restaurant/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPageController {

    private final ReservationService reservationService;
    private static final String MENU_DIR = "src/main/resources/static/menu";
    private static final String MENU_FILE_NAME = "menu.pdf";


    public AdminPageController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }
    @GetMapping
    public String showAdminPanel(Model model) {
        model.addAttribute("reservationForm", new ReservationFormDto());
        return "admin-panel";
    }


    @PostMapping("/reserve")
    public String createdReservationFromAdmin(@ModelAttribute ReservationFormDto form, Model model) {
        try {
            Reservation reservation = reservationService.createReservation(form);
            model.addAttribute("message", "Reservation created successfully!" +
                    "Reservation code is: " + reservation.getReservationCode());
        } catch (MessagingException | IllegalArgumentException e) {
            model.addAttribute("message", "Error: " + e.getMessage());
            model.addAttribute("reservationForm", form);
        } catch (NoAvailableTableException exception) {
            model.addAttribute("message", exception.getMessage());
            model.addAttribute("reservationForm", form);
        }
        // reset the form
        model.addAttribute("reservationForm", new ReservationFormDto());
        return "admin-panel";
    }

    @PostMapping("/cancel")
    public String cancelReservationFromAdmin(@RequestParam String reservationCode, Model model) {
        boolean success = reservationService.cancelReservation(reservationCode);

        if (success) {
            model.addAttribute("message", "Reservation cancelled successfully!");
        } else {
            model.addAttribute("message", "Reservation Code is not found");
        }
        model.addAttribute("reservationCode", "");
        model.addAttribute("reservationForm", new ReservationFormDto());
        return "admin-panel";
    }

    @GetMapping("/reservations/today")
    public String getReservationsToday(@RequestParam Integer tableNumber, Model model) {
        List<Reservation> tableReservations = reservationService.getReservationsForTableToday(tableNumber);
        if (tableReservations.isEmpty()) {
            model.addAttribute("message", "No reservations found for table " + tableNumber);
        }
        model.addAttribute("tableReservations", tableReservations);
        model.addAttribute("reservationForm", new ReservationFormDto());
        return "admin-panel";
    }

//    @GetMapping("/reservations/confirmed/by-date")
//    public String getReservationsConfirmedByDate(@RequestParam LocalDate date, Model model) {
//       List<Reservation> reservationsByDate = reservationService.getAllConfirmedReservationByDay(date);
//        if (reservationsByDate.isEmpty()) {
//            model.addAttribute("message", "No reservations found for date: " + date);
//            model.addAttribute("reservationForm", new ReservationFormDto());
//        }
//        model.addAttribute("allReservations", reservationsByDate);
//        return "admin-panel";
//    }

    @PostMapping("/upload-menu")
    public String uploadMenu(@RequestParam("file") MultipartFile file, Model model) {
        if (file.isEmpty() || !file.getOriginalFilename().endsWith(".pdf")) {
            model.addAttribute("message", "Invalid file");
            model.addAttribute("reservationForm", new ReservationFormDto());
            return "admin-panel";
        }
        try {
            Path path = Paths.get(MENU_DIR);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            Path filePath = path.resolve(MENU_FILE_NAME);
            Files.write(filePath, file.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            model.addAttribute("message", "Menu uploaded successfully!");
        } catch (IOException e) {
            model.addAttribute("message", "Error: " + e.getMessage());
            model.addAttribute("reservationForm", new ReservationFormDto());
            throw new RuntimeException(e);
        }
        model.addAttribute("reservationForm", new ReservationFormDto());
        return "admin-panel";
    }
}
