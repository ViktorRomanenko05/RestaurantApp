package de.ait.restaurantapp.controller;

import de.ait.restaurantapp.enums.ReservationStatus;
import de.ait.restaurantapp.model.Reservation;
import de.ait.restaurantapp.model.RestaurantTable;
import de.ait.restaurantapp.repositories.ReservationRepo;
import de.ait.restaurantapp.repositories.RestaurantTableRepo;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RestaurantPageControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestaurantTableRepo tableRepo;

    @Autowired
    private ReservationRepo reservationRepo;


    @BeforeEach
    void setUp() throws MessagingException {
        reservationRepo.deleteAll();
        tableRepo.deleteAll();

        // создаём 10 столов
        for (int i = 1; i <= 10; i++) {
            tableRepo.save(RestaurantTable.builder()
                    .capacity(i)
                    .build());
        }
    }

    @Test
    @DisplayName("Get reservation form")
    void getReserveForm() throws Exception {
        mockMvc.perform(get("/restaurant/reserve"))
                .andExpect(status().isOk())
                .andExpect(view().name("reservation-form"));
    }

    @Test
    @DisplayName("Successfull reservation")
    void postReserveSuccess() throws Exception {
        LocalDateTime tomorrow = LocalDate.now().plusDays(1).atTime(10, 0);
        String start = tomorrow.toString();

        mockMvc.perform(post("/restaurant/reserve")
                        .param("customerName", "John Doe")
                        .param("customerEmail", "john@mail.com")
                        .param("guestNumber", "2")
                        .param("startDateTime", start)
                        .param("endTime", "12:00")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("reservation-form"))
                .andExpect(model().attribute("message",
                        containsString("Reservation created successfully")));

        assertEquals(1, reservationRepo.findAll().size());
    }

    @Test
    @DisplayName("Reservation with no free tables")
    void reservationWithNoTablesTest() throws Exception {
        LocalDateTime dt = LocalDate.now().plusDays(1).atTime(10, 0);
        String start = dt.toString();

        // два брони по 10 гостей займут стол capacity=10
        for (int i = 0; i < 2; i++) {
            mockMvc.perform(post("/restaurant/reserve")
                            .param("customerName", "User" + i)
                            .param("customerEmail", "user" + i + "@ex.com")
                            .param("guestNumber", "9")
                            .param("startDateTime", start)
                            .param("endTime", "12:00")
                    )
                    .andExpect(status().isOk());
        }

        // добавление третьего бронирования (неудачное)
        mockMvc.perform(post("/restaurant/reserve")
                        .param("customerName", "User3")
                        .param("customerEmail", "user3@ex.com")
                        .param("guestNumber", "10")
                        .param("startDateTime", start)
                        .param("endTime", "12:00")
                )
                .andExpect(status().isOk())
                .andExpect(model().attribute("message",
                        containsString("No available tables")));
    }
    @Test
    @DisplayName("Get cancelling form")
    void getCancelFormTest() throws Exception {
        mockMvc.perform(get("/restaurant/cancel"))
                .andExpect(status().isOk())
                .andExpect(view().name("cancel-form"));
    }

    @Test
    @DisplayName("Cancelling successfull")
    void postCancelSuccessTest() throws Exception {
        // подготовить одну бронировку
        RestaurantTable table = tableRepo.findAll().get(0);
        Reservation reservation = Reservation.builder()
                .reservationCode("CODE123")
                .customerName("User")
                .customerEmail("user@mail.com")
                .guestCount(2)
                .startDateTime(LocalDate.now().plusDays(1).atTime(10, 0))
                .endDateTime(LocalDate.now().plusDays(1).atTime(12, 0))
                .restaurantTable(table)
                .reservationStatus(ReservationStatus.CONFIRMED)
                .isAdmin(false)
                .build();
        reservationRepo.save(reservation);
        mockMvc.perform(post("/restaurant/cancel")
                        .param("reservationCode", "CODE123")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("cancel-form"))
                .andExpect(model().attribute("message", "Reservation cancelled successfully!"));
        Reservation updated = reservationRepo.findById(reservation.getId()).orElseThrow();
        assertEquals(ReservationStatus.CANCELED, updated.getReservationStatus());
    }

    @Test
    @DisplayName("Reservation cancelling with wrong code")
    void reservationCancellingWithWrongCodeTest() throws Exception {
        RestaurantTable table = tableRepo.findAll().get(0);
        Reservation reservation = Reservation.builder()
                .reservationCode("REAL_CODE")
                .customerName("TestUser")
                .customerEmail("test@user.com")
                .guestCount(2)
                .startDateTime(LocalDate.now().plusDays(1).atTime(10, 0))
                .endDateTime(LocalDate.now().plusDays(1).atTime(12, 0))
                .restaurantTable(table)
                .reservationStatus(ReservationStatus.CONFIRMED)
                .isAdmin(false)
                .build();
        reservationRepo.save(reservation);
        mockMvc.perform(post("/restaurant/cancel")
                        .param("reservationCode", "WRONG_CODE")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("cancel-form"))
                .andExpect(model().attribute("message", "Reservation Code is not found"));
        Reservation stillThere = reservationRepo.findById(reservation.getId()).orElseThrow();
        assertEquals(1, reservationRepo.findAll().size());
        assertEquals(ReservationStatus.CONFIRMED, stillThere.getReservationStatus());
    }

    @Test
    @DisplayName("Get restaurant page")
    void getHomePage() throws Exception {
        mockMvc.perform(get("/restaurant"))
                .andExpect(status().isOk())
                .andExpect(view().name("homepage"));
    }
}