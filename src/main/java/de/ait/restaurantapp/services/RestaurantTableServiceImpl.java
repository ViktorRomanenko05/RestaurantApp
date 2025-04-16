package de.ait.restaurantapp.services;

import de.ait.restaurantapp.model.RestaurantTable;
import de.ait.restaurantapp.repositories.RestaurantTableRepo;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// This service let admin to "CRUD" tables
@Service
public class RestaurantTableServiceImpl implements RestaurantTableService {

    private final RestaurantTableRepo restaurantTableRepo;

    public RestaurantTableServiceImpl(RestaurantTableRepo restaurantTableRepo) {
        this.restaurantTableRepo = restaurantTableRepo;
    }

    @Override
    public Optional<RestaurantTable> getTableById(Long id) {
        return restaurantTableRepo.findById(id);
    }

    @Override
    public List<RestaurantTable> getAllTables() {
        return restaurantTableRepo.findAll();
    }

    @Override
    public List<RestaurantTable> initTables(int tablesCount) {
        List<RestaurantTable> tables = IntStream
                .rangeClosed(1, tablesCount).mapToObj(count -> RestaurantTable.builder()
                    .capacity(count)
                    .build())
                .collect(Collectors.toList());

        restaurantTableRepo.saveAll(tables);
        return tables;
    }

    @Override
    public RestaurantTable addTable(int capacity) {
        RestaurantTable newTable = RestaurantTable.builder().capacity(capacity).build();
        return restaurantTableRepo.save(newTable);
    }

    @Override
    public Optional<RestaurantTable> changeTableCapacityById(Long id, int capacity) {
        Optional<RestaurantTable> tableOptional = restaurantTableRepo.findById(id);
        if (!tableOptional.isPresent()) {
            return Optional.empty();
        }

        RestaurantTable table = tableOptional.get();
        table.setCapacity(capacity);

        restaurantTableRepo.save(table);
        return Optional.of(table);
    }

    @Override
    public void deleteTableById(Long id) {
        restaurantTableRepo.deleteById(id);
    }

    @Override
    public void deleteAllTables() {
        restaurantTableRepo.deleteAll();
    }
}
