package de.ait.restaurantapp.services;

import de.ait.restaurantapp.model.RestaurantTable;
import de.ait.restaurantapp.repositories.RestaurantTableRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class RestaurantTableServiceImplTests {

    @Autowired
    private RestaurantTableServiceImpl tableService;

    @Autowired
    private RestaurantTableRepo tableRepo;

    @BeforeEach
    void setUp() {
        tableRepo.deleteAll();
    }

    @Test
    @DisplayName("Tables initialisation")
    void initTablesPopulatesCorrectNumber() {
        assertEquals(0, tableRepo.count());
        tableService.initTables();
        assertEquals(10, tableRepo.count());
        List<Integer> capacities = tableRepo.findAll().stream()
                .map(RestaurantTable::getCapacity)
                .sorted()
                .collect(Collectors.toList());
        assertIterableEquals(List.of(1,2,3,4,5,6,7,8,9,10), capacities);
    }

    @Test
    @DisplayName("Init tables do not make tables duplicate")
    void initTablesIdempotent() {
        tableService.initTables();
        long firstCount = tableRepo.count();
        tableService.initTables();
        long secondCount = tableRepo.count();
        assertEquals(firstCount, secondCount);
    }

//    @Test
//    @DisplayName("Get table by id")
//    void getTableByIdExists() {
//        tableService.initTables();
//        Optional<RestaurantTable> third = tableService.getTableById(3L);
//        assertTrue(third.isPresent());
//        assertEquals(3, third.get().getCapacity());
//    }

    @Test
    @DisplayName("Get table by id negative")
    void getTableByIdNotExists() {
        tableService.initTables();
        Optional<RestaurantTable> none = tableService.getTableById(997L);
        assertFalse(none.isPresent());
    }

    @Test
    @DisplayName("GetAllTables")
    void getAllTables() {
        tableService.initTables();
        List<RestaurantTable> all = tableService.getAllTables();
        assertEquals(10, all.size());

        List<Integer> sortedCapacities = all.stream()
                .map(RestaurantTable::getCapacity)
                .sorted()
                .toList();
        assertIterableEquals(List.of(1,2,3,4,5,6,7,8,9,10), sortedCapacities);
    }
}