package com.jemberdin.votingsystem.service;

import com.jemberdin.votingsystem.model.Restaurant;
import com.jemberdin.votingsystem.util.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import javax.validation.ConstraintViolationException;

import java.util.List;

import static com.jemberdin.votingsystem.RestaurantTestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RestaurantServiceTest extends AbstractServiceTest {

    @Autowired
    protected RestaurantService service;

    @Test
    void create() throws Exception {
        Restaurant newRestaurant = getNew();
        Restaurant created = service.create(newRestaurant);
        Integer newId = created.getId();
        newRestaurant.setId(newId);
        RESTAURANT_MATCHERS.assertMatch(created, newRestaurant);
        RESTAURANT_MATCHERS.assertMatch(service.get(newId), created);
    }

    @Test
    void duplicateNameCreate() throws Exception {
        assertThrows(DataAccessException.class, () ->
                service.create(new Restaurant(null, RESTAURANT1.getName())));
    }

    @Test
    void delete() throws Exception {
        service.delete(RESTAURANT1_ID);
        assertThrows(NotFoundException.class, () ->
                service.delete(RESTAURANT1_ID));
    }

    @Test
    void deleteNotFound() throws Exception {
        assertThrows(NotFoundException.class, () ->
                service.delete(1));
    }

    @Test
    void get() throws Exception {
        Restaurant restaurant = service.get(RESTAURANT1_ID);
        RESTAURANT_MATCHERS.assertMatch(restaurant, RESTAURANT1);
    }

    @Test
    void getNotFound() throws Exception {
        assertThrows(NotFoundException.class, () ->
                service.get(1));
    }

    @Test
    void getByName() throws Exception {
        Restaurant restaurant = service.getByName(RESTAURANT1.getName());
        RESTAURANT_MATCHERS.assertMatch(restaurant, RESTAURANT1);
    }

    @Test
    void update() throws Exception {
        Restaurant updated = getUpdated();
        service.update(updated);
        RESTAURANT_MATCHERS.assertMatch(service.get(RESTAURANT1_ID), updated);
    }

    @Test
    void getAll() throws Exception {
        List<Restaurant> all = service.getAll();
        RESTAURANT_MATCHERS.assertMatch(all, RESTAURANT2, RESTAURANT4, RESTAURANT1, RESTAURANT3);
    }

    @Test
    void createWithException() throws Exception {
        validateRootCause(() -> service.create(new Restaurant(null, "  ")), ConstraintViolationException.class);
    }

    @Test
    void getAllWithVotes() throws Exception {
        List<Restaurant> restaurants = service.getAllWithVotes();
        RESTAURANT_MATCHERS.assertMatch(restaurants, RESTAURANT1, RESTAURANT2, RESTAURANT4, RESTAURANT3);

        assertEquals(4, restaurants.get(0).getVotes().size());
        assertEquals(RESTAURANT1.getName(), restaurants.get(0).getName());

        assertEquals(0, restaurants.get(1).getVotes().size());
        assertEquals(RESTAURANT2.getName(), restaurants.get(1).getName());

        assertEquals(0, restaurants.get(2).getVotes().size());
        assertEquals(RESTAURANT4.getName(), restaurants.get(2).getName());

        assertEquals(0, restaurants.get(3).getVotes().size());
        assertEquals(RESTAURANT3.getName(), restaurants.get(3).getName());
    }
}
