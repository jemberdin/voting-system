package com.jemberdin.votingsystem.repository;

import com.jemberdin.votingsystem.model.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public class MenuRepository {

    private CrudMenuRepository crudMenuRepository;

    private CrudRestaurantRepository crudRestaurantRepository;

    @Autowired
    public MenuRepository(CrudMenuRepository crudMenuRepository, CrudRestaurantRepository crudRestaurantRepository) {
        this.crudMenuRepository = crudMenuRepository;
        this.crudRestaurantRepository = crudRestaurantRepository;
    }

    public List<Menu> getAll(int restaurantId) {
        return crudMenuRepository.getAll(restaurantId);
    }

    public boolean delete(int id, int restaurantId) {
        return crudMenuRepository.delete(id, restaurantId) != 0;
    }

    @Transactional
    public Menu save(Menu menu, int restaurantId) {
        if (!menu.isNew() && get(menu.getId(), restaurantId) == null) {
            return null;
        }
        menu.setRestaurant(crudRestaurantRepository.getOne(restaurantId));
        return crudMenuRepository.save(menu);
    }

    public Menu get(int id, int restaurantId) {
        return crudMenuRepository.findById(id).filter(meal -> meal.getRestaurant().getId() == restaurantId).orElse(null);
    }

    public Menu getByDate(int restaurantId, LocalDate date) {
        return crudMenuRepository.getByDate(restaurantId, date);
    }

    public List<Menu> getAllTodayWithRestaurantAndDishes(LocalDate date) {
        return crudMenuRepository.getAllTodayWithRestaurantAndDishes(date);
    }
}
