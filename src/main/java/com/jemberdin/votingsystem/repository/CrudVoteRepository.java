package com.jemberdin.votingsystem.repository;

import com.jemberdin.votingsystem.model.Vote;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface CrudVoteRepository extends JpaRepository<Vote, Integer> {

    @Query("SELECT v FROM Vote v WHERE v.user.id=:userId ORDER BY v.date DESC")
    List<Vote> getAll(@Param("userId") int userId);

    @Override
    @Transactional
    Vote save(Vote item);

    @Query("SELECT v FROM Vote v JOIN FETCH v.restaurant WHERE v.id = ?1 AND v.user.id = ?2")
    Vote getWithRestaurant(int id, int userId);

    @EntityGraph(attributePaths = {"restaurant"})
    @Query("SELECT v FROM Vote v WHERE v.user.id=:userId ORDER BY v.date DESC")
    List<Vote> getAllWithRestaurant(@Param("userId") int userId);
}
