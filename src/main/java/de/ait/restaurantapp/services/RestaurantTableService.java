package de.ait.restaurantapp.services;

import de.ait.restaurantapp.model.RestaurantTable;

import java.util.List;
import java.util.Optional;

public interface RestaurantTableService {

    Optional<RestaurantTable> getTableById(Long id);

    List<RestaurantTable> getAllTables();

    List<RestaurantTable> initTables(int tablesCount);

    RestaurantTable addTable(int capacity);

    Optional<RestaurantTable> changeTableCapacityById(Long id, int capacity);

    void deleteTableById(Long id);

    void deleteAllTables();
}
