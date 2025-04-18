package de.ait.restaurantapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/restaurant")
public class RestaurantPageController {

    @GetMapping
    public String showHomePage(){
        return "homepage";
    }

    @GetMapping("/reserve")
    public String showReservationPage(){
        return "reservation-form";
    }

    @GetMapping("/cancel")
    public String showCancelPage(){
        return "cancel-form";
    }

    @GetMapping("/admin")
    public String showAdminPage(){
        return "admin-form";
    }
}
