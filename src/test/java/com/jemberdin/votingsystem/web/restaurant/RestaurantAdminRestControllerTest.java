package com.jemberdin.votingsystem.web.restaurant;

import com.jemberdin.votingsystem.RestaurantTestData;
import com.jemberdin.votingsystem.model.Restaurant;
import com.jemberdin.votingsystem.service.RestaurantService;
import com.jemberdin.votingsystem.util.exception.ErrorType;
import com.jemberdin.votingsystem.util.exception.NotFoundException;
import com.jemberdin.votingsystem.web.AbstractControllerTest;
import com.jemberdin.votingsystem.web.json.JsonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.jemberdin.votingsystem.RestaurantTestData.*;
import static com.jemberdin.votingsystem.TestUtil.readFromJson;
import static com.jemberdin.votingsystem.TestUtil.userHttpBasic;
import static com.jemberdin.votingsystem.UserTestData.ADMIN;
import static com.jemberdin.votingsystem.UserTestData.USER1;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RestaurantAdminRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = RestaurantAdminRestController.REST_URL + '/';

    private RestaurantService service;

    @Autowired
    public RestaurantAdminRestControllerTest(RestaurantService service) {
        this.service = service;
    }

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
    void deleteNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + 1)
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
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

    @Test
    void createInvalid() throws Exception {
        Restaurant invalid = new Restaurant(null, null);
        ResultActions action = mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid))
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value(ErrorType.VALIDATION_ERROR.name()))
                .andDo(print());
    }

    @Test
    void updateInvalid() throws Exception {
        Restaurant invalid = new Restaurant(RESTAURANT1_ID, null);
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid))
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value(ErrorType.VALIDATION_ERROR.name()));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void updateDuplicate() throws Exception {
        Restaurant invalid = new Restaurant(RESTAURANT1_ID, RESTAURANT2.getName());
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid))
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value(ErrorType.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("$.details").value("There is already a restaurant with this name"));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void createDuplicate() throws Exception {
        Restaurant invalid = new Restaurant(null, RESTAURANT1.getName());
        mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid))
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value(ErrorType.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("$.details").value("There is already a restaurant with this name"));
    }

    @Test
    void updateHtmlUnsafe() throws Exception {
        Restaurant invalid = new Restaurant(RESTAURANT1_ID, "<script>alert(123)</script>");
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid))
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value(ErrorType.VALIDATION_ERROR.name()))
                .andDo(print());
    }
}
