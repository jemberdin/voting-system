package com.jemberdin.votingsystem.repository;

import com.jemberdin.votingsystem.model.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public class VoteRepository {

    private CrudVoteRepository crudVoteRepository;

    private CrudUserRepository crudUserRepository;

    private CrudRestaurantRepository crudRestaurantRepository;

    @Autowired
    public VoteRepository(CrudVoteRepository crudVoteRepository, CrudUserRepository crudUserRepository, CrudRestaurantRepository crudRestaurantRepository) {
        this.crudVoteRepository = crudVoteRepository;
        this.crudUserRepository = crudUserRepository;
        this.crudRestaurantRepository = crudRestaurantRepository;
    }

    public List<Vote> getAll(int userId) {
        return crudVoteRepository.getAll(userId);
    }

    @Transactional
    public Vote save(Vote vote, int userId, int restaurantId, LocalDate date) {
        if (!vote.isNew() && get(vote.getId(), userId) == null) {
            return null;
        }
        vote.setUser(crudUserRepository.getOne(userId));
        vote.setRestaurant(crudRestaurantRepository.getOne(restaurantId));
        vote.setDate(date);
        return crudVoteRepository.save(vote);
    }

    public Vote get(int id, int userId) {
        return crudVoteRepository.findById(id).filter(vote -> vote.getUser().getId() == userId).orElse(null);
    }

    public Vote getWithRestaurant(int id, int userId) {
        return crudVoteRepository.getWithRestaurant(id, userId);
    }

    public List<Vote> getAllWithRestaurant(int userId) {
        return crudVoteRepository.getAllWithRestaurant(userId);
    }
}
