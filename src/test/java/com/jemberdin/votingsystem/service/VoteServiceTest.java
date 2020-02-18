package com.jemberdin.votingsystem.service;

import com.jemberdin.votingsystem.RestaurantTestData;
import com.jemberdin.votingsystem.VoteTestData;
import com.jemberdin.votingsystem.model.Vote;
import com.jemberdin.votingsystem.repository.VoteRepository;
import com.jemberdin.votingsystem.util.exception.NotFoundException;
import com.jemberdin.votingsystem.util.exception.VotingDateException;
import com.jemberdin.votingsystem.util.exception.VotingTimeException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.time.LocalTime;

import static com.jemberdin.votingsystem.RestaurantTestData.*;
import static com.jemberdin.votingsystem.UserTestData.*;
import static com.jemberdin.votingsystem.VoteTestData.*;
import static com.jemberdin.votingsystem.util.DateTimeUtil.DATE_TIME_FOR_TEST_AFTER;
import static com.jemberdin.votingsystem.util.DateTimeUtil.FINISHING_UPDATE_VOTE_TIME;
import static com.jemberdin.votingsystem.util.ValidationUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VoteServiceTest extends AbstractServiceTest {

    protected VoteService service;

    protected VoteRepository repository;

    @Autowired
    public VoteServiceTest(VoteService service, VoteRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @Test
    void create() throws Exception {
        Vote newVote = getNewVote();
        Vote created = service.create(USER2.getId(), RESTAURANT2_ID);
        Integer newId = created.getId();
        newVote.setId(newId);
        VOTE_MATCHERS.assertMatch(created, newVote);
        VOTE_MATCHERS.assertMatch(service.get(newId, USER2.getId()), newVote);
    }

    @Test
    void createWithSameDate() throws Exception {
        assertThrows(DataIntegrityViolationException.class, () ->
                service.create(USER1_ID, RESTAURANT2_ID));
    }

    @Test
    void createWithNoMenuFound() throws Exception {
        assertThrows(NotFoundException.class, () ->
                service.create(USER2.getId(), RESTAURANT4_ID));
    }

    @Test
    void get() throws Exception {
        Vote actual = service.get(VOTE1_ID, USER1_ID);
        VOTE_MATCHERS.assertMatch(actual, VOTE1);
    }

    @Test
    void getAll() throws Exception {
        VOTE_MATCHERS.assertMatch(service.getAll(USER1_ID), VOTE1, VOTE2);
    }

    @Test
    void getWithRestaurant() throws Exception {
        Vote vote = service.getWithRestaurant(VOTE1_ID, USER1_ID);
        VOTE_MATCHERS.assertMatch(vote, VOTE1);
        RestaurantTestData.RESTAURANT_MATCHERS.assertMatch(vote.getRestaurant(), RESTAURANT1);
    }

    @Test
    void getAllWithRestaurant() throws Exception {
        VOTE_MATCHERS.assertMatch(service.getAllWithRestaurant(USER1_ID), VOTE1, VOTE2);
    }

    @Test
    void getNotFound() throws Exception {
        assertThrows(NotFoundException.class, () -> service.get(1, USER1_ID));
    }

    @Test
    void getNotOwn() throws Exception {
        assertThrows(NotFoundException.class, () -> service.get(VOTE3_ID, USER1_ID));
    }

    @Test
    void update() throws Exception {
        Vote updated = VoteTestData.getUpdated();

        if (LocalTime.now().isBefore(FINISHING_UPDATE_VOTE_TIME)) {
            service.update(updated.getId(), USER1_ID, RESTAURANT2_ID);
        } else {
            repository.save(updated, USER1_ID, RESTAURANT2_ID, LocalDate.now());
        }
        Vote vote = service.getWithRestaurant(VOTE1_ID, USER1_ID);
        VOTE_MATCHERS.assertMatch(vote, updated);
        RestaurantTestData.RESTAURANT_MATCHERS.assertMatch(vote.getRestaurant(), RESTAURANT2);
    }

    @Test
    void updateNotFound() throws Exception {
        Vote updated = VoteTestData.getUpdated();
        NotFoundException e;

        if (LocalTime.now().isBefore(FINISHING_UPDATE_VOTE_TIME)) {
            e = assertThrows(NotFoundException.class,
                    () -> service.update(updated.getId(), USER2.getId(), RESTAURANT1_ID));
        } else {
            e = assertThrows(NotFoundException.class,
                    () -> checkNotFoundWithId(repository.save(
                            updated, USER2.getId(), RESTAURANT1_ID, LocalDate.now()), updated.getId()));
        }
        assertEquals(e.getMessage(), "Not found entity with id=" + VOTE1_ID);
    }

    @Test
    void updateAfterFinishingVoteTime() throws Exception {
        VotingTimeException e;
        if (LocalTime.now().isBefore(FINISHING_UPDATE_VOTE_TIME)) {
            e = assertThrows(VotingTimeException.class, () ->
                    checkTimeToUpdateVote(DATE_TIME_FOR_TEST_AFTER.toLocalTime()));
        } else {
            Vote updated = VoteTestData.getUpdated();
            e = assertThrows(VotingTimeException.class, () ->
                    service.update(updated.getId(), USER1_ID, RESTAURANT2_ID));
        }
        assertEquals(e.getMessage(), "Update is not allowed after " + FINISHING_UPDATE_VOTE_TIME);
    }

    @Test
    void updateForPastDate() throws Exception {
        Vote updated = VOTE2;
        if (LocalTime.now().isBefore(FINISHING_UPDATE_VOTE_TIME)) {
            assertThrows(VotingDateException.class, () ->
                    service.update(updated.getId(), USER1.getId(), RESTAURANT2_ID));
        } else {
            assertThrows(VotingDateException.class, () ->
                    checkVotingDate(updated.getDate(), LocalDate.now()));
        }
    }
}
