package de.ait.restaurantapp.services;

import de.ait.restaurantapp.model.RestaurantTable;
import de.ait.restaurantapp.repositories.RestaurantTableRepo;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class RestaurantTableServiceImpl implements RestaurantTableService {

    // Edit application.properties restaurant.table-count for change table count
    @Value("${restaurant.table-count:0}")
    private int tableCount;

    private final RestaurantTableRepo restaurantTableRepo;

    public RestaurantTableServiceImpl(RestaurantTableRepo restaurantTableRepo) {
        this.restaurantTableRepo = restaurantTableRepo;
    }

    @PostConstruct // to run fixed table count before dependency injection. it initializes our tables in database
    public void initTables() {
        if(restaurantTableRepo.count() == 0) {
            List<RestaurantTable> tables = IntStream
                    .rangeClosed(1, tableCount).mapToObj(count -> RestaurantTable.builder()
                            .capacity(count)
                            .build())
                    .collect(Collectors.toList());

            log.info("Tables initialized: {}", tables);
            restaurantTableRepo.saveAll(tables);
        }
    }

    @Override
    public Optional<RestaurantTable> getTableById(Long id) {
        log.debug("Looking for table with id: {}", id);
        Optional<RestaurantTable> table = restaurantTableRepo.findById(id);
        if(table.isPresent()) {
            log.info("Found table with id: {}", id);
        } else {
            log.warn("No table found with id: {}", id);
        }
        return table;
    }

    @Override
    public List<RestaurantTable> getAllTables() {
        log.debug("Fetching  all tables.");
        List<RestaurantTable> tables = restaurantTableRepo.findAll();
        if(!tables.isEmpty()) {
            log.debug("Retrieved {} tables.", tables.size());
        } else {
            log.warn("No tables found.");
        }
        return tables;
    }
}
