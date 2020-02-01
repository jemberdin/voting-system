package com.jemberdin.votingsystem.repository;

import com.jemberdin.votingsystem.model.Restaurant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface CrudRestaurantRepository extends JpaRepository<Restaurant, Integer> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Restaurant r WHERE r.id=:id")
    int delete(@Param("id") int id);

    Restaurant getByName(String name);

    @Query("SELECT r FROM Restaurant r ORDER BY size(r.votes) DESC, r.name ASC")
    @EntityGraph(attributePaths = {"votes"})
    List<Restaurant> getAllWithVotes();
}
