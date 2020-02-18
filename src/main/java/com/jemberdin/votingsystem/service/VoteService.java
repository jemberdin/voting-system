package com.jemberdin.votingsystem.service;

import com.jemberdin.votingsystem.model.Menu;
import com.jemberdin.votingsystem.model.Vote;
import com.jemberdin.votingsystem.repository.MenuRepository;
import com.jemberdin.votingsystem.repository.VoteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.jemberdin.votingsystem.util.ValidationUtil.*;

@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final MenuRepository menuRepository;

    public VoteService(VoteRepository repository, MenuRepository menuRepository) {
        this.voteRepository = repository;
        this.menuRepository = menuRepository;
    }

    public List<Vote> getAll(int userId) {
        return voteRepository.getAll(userId);
    }

    public Vote get(int id, int userId) {
        return checkNotFoundWithId(voteRepository.get(id, userId), id);
    }

    public Vote create(int userId, int restaurantId) {
        Menu menu = menuRepository.getByDate(restaurantId, LocalDate.now());
        checkNotFound(menu, "Menu to date=" + LocalDate.now());
        return voteRepository.save(new Vote(), userId, restaurantId, LocalDate.now());
    }

    public void update(int id, int userId, int restaurantId) {
        Vote vote = get(id, userId);
        checkTimeToUpdateVote(LocalDateTime.now().toLocalTime());
        checkVotingDate(vote.getDate(), LocalDate.now());
        checkNotFoundWithId(voteRepository.save(vote, userId, restaurantId, LocalDate.now()), vote.getId());
    }

    public Vote getWithRestaurant(int id, int userId) {
        return checkNotFoundWithId(voteRepository.getWithRestaurant(id, userId), id);
    }

    public List<Vote> getAllWithRestaurant(int userId) {
        return voteRepository.getAllWithRestaurant(userId);
    }
}
