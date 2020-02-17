package com.jemberdin.votingsystem.web.restaurant;

import com.jemberdin.votingsystem.RestaurantTestData;
import com.jemberdin.votingsystem.model.Restaurant;
import com.jemberdin.votingsystem.service.RestaurantService;
import com.jemberdin.votingsystem.util.exception.NotFoundException;
import com.jemberdin.votingsystem.web.AbstractControllerTest;
import com.jemberdin.votingsystem.web.json.JsonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.jemberdin.votingsystem.RestaurantTestData.RESTAURANT1_ID;
import static com.jemberdin.votingsystem.RestaurantTestData.RESTAURANT_MATCHERS;
import static com.jemberdin.votingsystem.TestUtil.readFromJson;
import static com.jemberdin.votingsystem.TestUtil.userHttpBasic;
import static com.jemberdin.votingsystem.UserTestData.ADMIN;
import static com.jemberdin.votingsystem.UserTestData.USER1;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RestaurantAdminRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = RestaurantAdminRestController.REST_URL + '/';

    @Autowired
    private RestaurantService service;

    @Test
    void createWithLocation() throws Exception {
        Restaurant newRestaurant = RestaurantTestData.getNew();
        ResultActions action = mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newRestaurant))
                .with(userHttpBasic(ADMIN)))
                .andDo(print());

        Restaurant created = readFromJson(action, Restaurant.class);
        Integer newId = created.getId();
        newRestaurant.setId(newId);
        RESTAURANT_MATCHERS.assertMatch(created, newRestaurant);
        RESTAURANT_MATCHERS.assertMatch(service.get(newId), newRestaurant);
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + RESTAURANT1_ID)
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> service.get(RESTAURANT1_ID));
    }

    @Test
    void update() throws Exception {
        Restaurant updated = RestaurantTestData.getUpdated();

        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated))
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isNoContent());

        RESTAURANT_MATCHERS.assertMatch(service.get(RESTAURANT1_ID), updated);
    }

    @Test
    void getUnAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + RESTAURANT1_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .with(userHttpBasic(USER1)))
                .andExpect(status().isForbidden());
    }
}
