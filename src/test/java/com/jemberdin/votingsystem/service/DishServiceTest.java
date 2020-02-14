package com.jemberdin.votingsystem.service;

import com.jemberdin.votingsystem.model.Dish;
import com.jemberdin.votingsystem.util.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintViolationException;

import static com.jemberdin.votingsystem.MenuTestData.MENU1_ID;
import static com.jemberdin.votingsystem.MenuTestData.MENU2_ID;
import static com.jemberdin.votingsystem.DishTestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DishServiceTest extends AbstractServiceTest {

    @Autowired
    protected DishService service;

    @Test
    void create() throws Exception {
        Dish newDish = getNew();
        Dish created = service.create(newDish, MENU1_ID);
        Integer newId = created.getId();
        newDish.setId(newId);
        DISH_MATCHERS.assertMatch(created, newDish);
        DISH_MATCHERS.assertMatch(service.get(newId, MENU1_ID), newDish);
    }

    @Test
    void delete() throws Exception {
        service.delete(DISH1_ID, MENU1_ID);
        assertThrows(NotFoundException.class, () ->
                service.get(DISH1_ID, MENU1_ID));
    }

    @Test
    void deleteNotFound() throws Exception {
        assertThrows(NotFoundException.class, () ->
                service.delete(1, MENU1_ID));
    }

    @Test
    void deleteNotOwn() throws Exception {
        assertThrows(NotFoundException.class, () ->
                service.delete(DISH1_ID, MENU2_ID));
    }

    @Test
    void getAll() throws Exception {
        DISH_MATCHERS.assertMatch(service.getAll(MENU1_ID), DISH1, DISH3, DISH2);
    }

    @Test
    void get() throws Exception {
        Dish dish = service.get(DISH1_ID, MENU1_ID);
        DISH_MATCHERS.assertMatch(dish, DISH1);
    }

    @Test
    void getNotFound() throws Exception {
        assertThrows(NotFoundException.class, () ->
                service.get(1, MENU2_ID));
    }

    @Test
    void getNotOwn() throws Exception {
        assertThrows(NotFoundException.class, () ->
                service.get(DISH1_ID, MENU2_ID));
    }

    @Test
    void update() throws Exception {
        Dish updated = getUpdated();
        service.update(updated, MENU1_ID);
        DISH_MATCHERS.assertMatch(service.get(DISH1_ID, MENU1_ID), updated);
    }

    @Test
    void updateNotFound() throws Exception {
        NotFoundException e = assertThrows(NotFoundException.class, () -> service.update(DISH1, MENU2_ID));
        assertEquals(e.getMessage(), "Not found entity with id=" + DISH1_ID);
    }

    @Test
    void createWithException() throws Exception {
        validateRootCause(() -> service.create(new Dish(null,"  ", 300), MENU1_ID), ConstraintViolationException.class);
        validateRootCause(() -> service.create(new Dish(null,"Name", -1), MENU1_ID), ConstraintViolationException.class);
        validateRootCause(() -> service.create(new Dish(null,"Name", 1_000_001), MENU1_ID), ConstraintViolationException.class);
    }
}
