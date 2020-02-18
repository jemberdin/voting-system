package com.jemberdin.votingsystem.web.vote;

import com.jemberdin.votingsystem.RestaurantTestData;
import com.jemberdin.votingsystem.VoteTestData;
import com.jemberdin.votingsystem.model.Vote;
import com.jemberdin.votingsystem.service.VoteService;
import com.jemberdin.votingsystem.web.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

import static com.jemberdin.votingsystem.RestaurantTestData.RESTAURANT2;
import static com.jemberdin.votingsystem.RestaurantTestData.RESTAURANT2_ID;
import static com.jemberdin.votingsystem.TestUtil.*;
import static com.jemberdin.votingsystem.UserTestData.*;
import static com.jemberdin.votingsystem.VoteTestData.*;
import static com.jemberdin.votingsystem.util.DateTimeUtil.FINISHING_UPDATE_VOTE_TIME;
import static com.jemberdin.votingsystem.util.exception.ErrorType.VALIDATION_ERROR;
import static com.jemberdin.votingsystem.web.ExceptionInfoHandler.EXCEPTION_DUPLICATE_VOTE;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class VoteRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = VoteRestController.REST_URL + '/';

    private VoteService service;

    @Autowired
    public VoteRestControllerTest(VoteService service) {
        this.service = service;
    }

    @Test
    void get() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + VOTE1_ID)
                .with(userHttpBasic(USER1)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> VOTE_MATCHERS.assertMatch(readFromJsonMvcResult(result, Vote.class), VOTE1));
    }

    @Test
    void getAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .with(userHttpBasic(USER1)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(VOTE_MATCHERS.contentJson(VOTE1, VOTE2));
    }

    @Test
    void getUnAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + VOTE1_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + VOTE1_ID)
                .with(userHttpBasic(USER2)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void getWithRestaurant() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + VOTE1_ID)
                .with(userHttpBasic(USER1)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> VOTE_MATCHERS.assertMatch(readFromJsonMvcResult(result, Vote.class), VOTE1));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void createWithLocation() throws Exception {
        Vote newVote = VoteTestData.getNewVote();
        ResultActions action = mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                .param("restaurantId", String.valueOf(RESTAURANT2_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(USER2)))
                .andDo(print());

        Vote created = readFromJson(action, Vote.class);
        Integer newId = created.getId();
        newVote.setId(newId);
        VOTE_MATCHERS.assertMatch(created, newVote);

        Vote actual = service.getWithRestaurant(newId, USER2.getId());
        VOTE_MATCHERS.assertMatch(service.get(newId, USER2.getId()), newVote);
        RestaurantTestData.RESTAURANT_MATCHERS.assertMatch(actual.getRestaurant(), RESTAURANT2);
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void update() throws Exception {
        if (LocalTime.now().isBefore(FINISHING_UPDATE_VOTE_TIME)) {
            Vote updated = VoteTestData.getUpdated();
            mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + VOTE1_ID)
                    .param("restaurantId", String.valueOf(RESTAURANT2_ID))
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(userHttpBasic(USER1)))
                    .andExpect(status().isNoContent())
                    .andDo(print());

            Vote vote = service.getWithRestaurant(VOTE1_ID, USER1_ID);
            VOTE_MATCHERS.assertMatch(vote, updated);
            RestaurantTestData.RESTAURANT_MATCHERS.assertMatch(vote.getRestaurant(), RESTAURANT2);
        } else {
            mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + VOTE1_ID)
                    .param("restaurantId", String.valueOf(RESTAURANT2_ID))
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(userHttpBasic(USER1)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.type").value(VALIDATION_ERROR.name()))
                    .andExpect(jsonPath("$.details").value(
                            "com.jemberdin.votingsystem.util.exception.VotingTimeException: " +
                                    "Update is not allowed after " + FINISHING_UPDATE_VOTE_TIME));
        }
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void updateAfterFinishingVoteTime() throws Exception {
        if (LocalTime.now().isAfter(FINISHING_UPDATE_VOTE_TIME)) {
            mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + VOTE1_ID)
                    .param("restaurantId", String.valueOf(RESTAURANT2_ID))
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(userHttpBasic(USER1)))
                    .andDo(print())
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.type").value(VALIDATION_ERROR.name()))
                    .andExpect(jsonPath("$.details").value(
                            "com.jemberdin.votingsystem.util.exception.VotingTimeException: " +
                                    "Update is not allowed after " + FINISHING_UPDATE_VOTE_TIME));
        } else {
            mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + VOTE1_ID)
                    .param("restaurantId", String.valueOf(RESTAURANT2_ID))
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(userHttpBasic(USER1)))
                    .andExpect(status().isNoContent());
        }
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void createWithSameDate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                .param("restaurantId", String.valueOf(RESTAURANT2_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(USER1)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value(VALIDATION_ERROR.name()))
                .andExpect(jsonPath("$.details").value(EXCEPTION_DUPLICATE_VOTE));
    }
}
