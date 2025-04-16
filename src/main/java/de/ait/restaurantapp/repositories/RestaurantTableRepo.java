package de.ait.restaurantapp.repositories;

import de.ait.restaurantapp.model.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantTableRepo extends JpaRepository<RestaurantTable, Long> {
}
