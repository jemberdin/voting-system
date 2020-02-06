package com.jemberdin.votingsystem.service;

import com.jemberdin.votingsystem.model.Vote;
import com.jemberdin.votingsystem.repository.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.List;

import static com.jemberdin.votingsystem.util.ValidationUtil.checkNotFoundWithId;

@Service
public class VoteService {

    private final VoteRepository repository;

    public VoteService(VoteRepository voteRepository) {
        this.repository = voteRepository;
    }

    public List<Vote> getAll(int userId) {
        return repository.getAll(userId);
    }

    public Vote get(int id, int userId) {
        return checkNotFoundWithId(repository.get(id, userId), id);
    }

    public Vote create(Vote vote, int userId, int restaurantId) {
        Assert.notNull(vote, "vote must not be null");
        return repository.save(vote, userId, restaurantId, LocalDate.now());
    }

    public void update(Vote vote, int userId, int restaurantId) {
        Assert.notNull(vote, "vote must not be null");
        checkNotFoundWithId(repository.save(vote, userId, restaurantId, LocalDate.now()), vote.getId());
    }

    public Vote getWithRestaurant(int id, int userId) {
        return checkNotFoundWithId(repository.getWithRestaurant(id, userId), id);
    }

    public List<Vote> getAllWithRestaurant(int userId) {
        return repository.getAllWithRestaurant(userId);
    }
}
