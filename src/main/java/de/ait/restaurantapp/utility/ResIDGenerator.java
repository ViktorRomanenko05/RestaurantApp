package de.ait.restaurantapp.utility;

import java.util.UUID;

public class ResIDGenerator {
    public static String generateReservationId() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String timestamp = String.valueOf(System.currentTimeMillis()); // Timestamp do ID
        return "RES-" + timestamp + "-" + uuid.substring(0, 8);
    }
}
