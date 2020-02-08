package com.jemberdin.votingsystem.service;

import com.jemberdin.votingsystem.model.Menu;
import com.jemberdin.votingsystem.repository.MenuRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.List;

import static com.jemberdin.votingsystem.util.ValidationUtil.*;

@Service
public class MenuService {

    private final MenuRepository repository;

    public MenuService(MenuRepository repository) {
        this.repository = repository;
    }

    public List<Menu> getAll(int restaurantId) {
        return repository.getAll(restaurantId);
    }

    public Menu get(int id, int restaurantId) {
        return checkNotFoundWithId(repository.get(id, restaurantId), id);
    }

    public Menu create(Menu menu, int restaurantId) {
        Assert.notNull(menu, "menu must not be null");
        checkCreateMenuForDate(menu.getDate());
        return repository.save(menu, restaurantId);
    }

    public void delete(int id, int restaurantId) {
        checkNotFoundWithId(repository.delete(id, restaurantId), id);
    }

    public void update(Menu menu, int restaurantId) {
        Assert.notNull(menu, "menu must not be null");
        checkCreateMenuForDate(menu.getDate());
        checkNotFoundWithId(repository.save(menu, restaurantId), menu.getId());
    }

    public Menu getByDate(int restaurantId, LocalDate date) {
        Assert.notNull(date, "date must not be null");
        return checkNotFoundWithDate(repository.getByDate(restaurantId, date), date);
    }

    public List<Menu> getAllTodayWithRestaurantAndDishes() {
        return repository.getAllTodayWithRestaurantAndDishes(LocalDate.now());
    }
}
