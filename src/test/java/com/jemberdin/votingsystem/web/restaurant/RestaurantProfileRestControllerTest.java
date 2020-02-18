package com.jemberdin.votingsystem.web.restaurant;

import com.jemberdin.votingsystem.model.Restaurant;
import com.jemberdin.votingsystem.service.RestaurantService;
import com.jemberdin.votingsystem.web.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.jemberdin.votingsystem.RestaurantTestData.*;
import static com.jemberdin.votingsystem.TestUtil.readFromJsonMvcResult;
import static com.jemberdin.votingsystem.TestUtil.userHttpBasic;
import static com.jemberdin.votingsystem.UserTestData.USER1;
import static com.jemberdin.votingsystem.util.RestaurantUtil.getTos;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RestaurantProfileRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = RestaurantProfileRestController.REST_URL + '/';

    private RestaurantService service;

    @Autowired
    public RestaurantProfileRestControllerTest(RestaurantService service) {
        this.service = service;
    }

    @Test
    void getAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .with(userHttpBasic(USER1)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESTAURANT_MATCHERS.contentJson(RESTAURANT2, RESTAURANT4, RESTAURANT1, RESTAURANT3));
    }

    @Test
    void get() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + RESTAURANT1_ID)
                .with(userHttpBasic(USER1)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> RESTAURANT_MATCHERS.assertMatch(readFromJsonMvcResult(result, Restaurant.class), RESTAURANT1));
    }

    @Test
    void getByName() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "by?name=" + RESTAURANT1.getName())
                .with(userHttpBasic(USER1)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> RESTAURANT_MATCHERS.assertMatch(readFromJsonMvcResult(result, Restaurant.class), RESTAURANT1));
    }

    @Test
    void getNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + 1)
                .with(userHttpBasic(USER1)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void getAllWithVotes() throws Exception {
        List<Restaurant> restaurants = service.getAllWithVotes();
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "result")
                .with(userHttpBasic(USER1)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESTAURANT_TO_MATCHERS.contentJson(getTos(restaurants)));
    }
}
