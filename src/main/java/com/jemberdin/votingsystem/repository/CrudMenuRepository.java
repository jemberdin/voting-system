package com.jemberdin.votingsystem.repository;

import com.jemberdin.votingsystem.model.Menu;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Transactional(readOnly = true)
public interface CrudMenuRepository extends JpaRepository<Menu, Integer> {

    @Query("SELECT m FROM Menu m WHERE m.restaurant.id=:restaurantId ORDER BY m.date DESC")
    List<Menu> getAll(@Param("restaurantId") int restaurantId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Menu m WHERE m.id=:id AND m.restaurant.id=:restaurantId")
    int delete(@Param("id") int id, @Param("restaurantId") int restaurantId);

    @Override
    @Transactional
    Menu save(Menu menu);

    @Query("SELECT m FROM Menu m WHERE m.restaurant.id=:restaurantId AND m.date=:date")
    Menu getByDate(@Param("restaurantId") int restaurantId, @Param("date") LocalDate date);

    @EntityGraph(attributePaths = {"dishes"}, type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT m FROM Menu m JOIN FETCH m.restaurant JOIN FETCH m.dishes WHERE m.date=:date ORDER BY m.restaurant.name")
    List<Menu> getAllTodayWithRestaurantAndDishes(@Param("date") LocalDate date);
}
