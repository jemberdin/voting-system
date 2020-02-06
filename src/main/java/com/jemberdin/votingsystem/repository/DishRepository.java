package com.jemberdin.votingsystem.repository;

import com.jemberdin.votingsystem.model.Dish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class DishRepository {

    private CrudDishRepository crudDishRepository;

    private CrudMenuRepository crudMenuRepository;

    @Autowired
    public DishRepository(CrudDishRepository crudDishRepository, CrudMenuRepository crudMenuRepository) {
        this.crudDishRepository = crudDishRepository;
        this.crudMenuRepository = crudMenuRepository;
    }

    public List<Dish> getAll(int menuId) {
        return crudDishRepository.getAll(menuId);
    }

    public boolean delete(int id, int menuId) {
        return crudDishRepository.delete(id, menuId) != 0;
    }

    @Transactional
    public Dish save(Dish dish, int menuId) {
        if (!dish.isNew() && get(dish.getId(), menuId) == null) {
            return null;
        }
        dish.setMenu(crudMenuRepository.getOne(menuId));
        return crudDishRepository.save(dish);
    }

    public Dish get(int id, int menuId) {
        return crudDishRepository.findById(id).filter(dish -> dish.getMenu().getId() == menuId).orElse(null);
    }
}
