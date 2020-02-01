package com.jemberdin.votingsystem.repository;

import com.jemberdin.votingsystem.model.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RestaurantRepository {

    private static final Sort SORT_NAME = Sort.by(Sort.Direction.ASC, "name");

    private CrudRestaurantRepository crudRepository;

    @Autowired
    public RestaurantRepository(CrudRestaurantRepository crudRepository) {
        this.crudRepository = crudRepository;
    }

    public Restaurant save(Restaurant restaurant) {
        return crudRepository.save(restaurant);
    }

    public boolean delete(int id) {
        return crudRepository.delete(id) != 0;
    }

    public Restaurant get(int id) {
        return crudRepository.findById(id).orElse(null);
    }

    public Restaurant getByName(String name) {
        return crudRepository.getByName(name);
    }

    public List<Restaurant> getAll() {
        return crudRepository.findAll(SORT_NAME);
    }

    public List<Restaurant> getAllWithVotes() {
        return crudRepository.getAllWithVotes();
    }
}
