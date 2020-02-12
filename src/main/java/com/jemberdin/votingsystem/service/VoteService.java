package com.jemberdin.votingsystem.service;

import com.jemberdin.votingsystem.model.Vote;
import com.jemberdin.votingsystem.repository.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.jemberdin.votingsystem.util.ValidationUtil.*;

@Service
public class VoteService {

    private final VoteRepository repository;

    public VoteService(VoteRepository repository) {
        this.repository = repository;
    }

    public List<Vote> getAll(int userId) {
        return repository.getAll(userId);
    }

    public Vote get(int id, int userId) {
        return checkNotFoundWithId(repository.get(id, userId), id);
    }

    public Vote create(int userId, int restaurantId) {
        return repository.save(new Vote(), userId, restaurantId, LocalDate.now());
    }

    public void update(int id, int userId, int restaurantId) {
        Vote vote = get(id, userId);
        checkTimeToUpdateVote(LocalDateTime.now().toLocalTime());
        checkVotingDate(vote.getDate(), LocalDate.now());
        checkNotFoundWithId(repository.save(vote, userId, restaurantId, LocalDate.now()), vote.getId());
    }

    public Vote getWithRestaurant(int id, int userId) {
        return checkNotFoundWithId(repository.getWithRestaurant(id, userId), id);
    }

    public List<Vote> getAllWithRestaurant(int userId) {
        return repository.getAllWithRestaurant(userId);
    }
}
