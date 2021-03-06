package com.jemberdin.votingsystem.web.dish;

import com.jemberdin.votingsystem.DishTestData;
import com.jemberdin.votingsystem.model.Dish;
import com.jemberdin.votingsystem.service.DishService;
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

import static com.jemberdin.votingsystem.DishTestData.*;
import static com.jemberdin.votingsystem.MenuTestData.MENU1_ID;
import static com.jemberdin.votingsystem.TestUtil.*;
import static com.jemberdin.votingsystem.UserTestData.ADMIN;
import static com.jemberdin.votingsystem.UserTestData.USER1;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class DishRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = DishRestController.REST_URL + '/';

    private DishService service;

    @Autowired
    public DishRestControllerTest(DishService service) {
        this.service = service;
    }

    @Test
    void get() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + DISH1_ID)
                .param("menuId", String.valueOf(MENU1_ID))
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> DISH_MATCHERS.assertMatch(readFromJsonMvcResult(result, Dish.class), DISH1));
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + DISH1_ID)
                .param("menuId", String.valueOf(MENU1_ID))
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isNoContent())
                .andDo(print());
        assertThrows(NotFoundException.class, () -> service.get(DISH1_ID, MENU1_ID));
    }

    @Test
    void deleteNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + DISH2_ID + 2)
                .param("menuId", String.valueOf(MENU1_ID))
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void getNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + DISH1_ID + 1)
                .param("menuId", String.valueOf(MENU1_ID))
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void getUnAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + DISH1_ID)
                .param("menuId", String.valueOf(MENU1_ID)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param("menuId", String.valueOf(MENU1_ID))
                .with(userHttpBasic(USER1)))
                .andExpect(status().isForbidden());
    }

    @Test
    void update() throws Exception {
        Dish updated = DishTestData.getUpdated();

        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + DISH1_ID)
                .param("menuId", String.valueOf(MENU1_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated))
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isNoContent())
                .andDo(print());

        DISH_MATCHERS.assertMatch(service.get(DISH1_ID, MENU1_ID), updated);
    }

    @Test
    void createWithLocation() throws Exception {
        Dish newDish = DishTestData.getNew();
        ResultActions action = mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                .param("menuId", String.valueOf(MENU1_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newDish))
                .with(userHttpBasic(ADMIN)))
                .andDo(print());

        Dish created = readFromJson(action, Dish.class);
        Integer newId = created.getId();
        newDish.setId(newId);
        DISH_MATCHERS.assertMatch(created, newDish);
        DISH_MATCHERS.assertMatch(service.get(newId, MENU1_ID), newDish);
    }

    @Test
    void getAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param("menuId", String.valueOf(MENU1_ID))
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DISH_MATCHERS.contentJson(DISH1, DISH3, DISH2));
    }

    @Test
    void updateInvalid() throws Exception {
        Dish invalid = new Dish(DISH1_ID, null, 5000);
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + DISH1_ID)
                .param("menuId", String.valueOf(MENU1_ID))
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
        Dish invalid = new Dish(DISH1_ID, DISH3.getName(), 1000);

        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + DISH1_ID)
                .param("menuId", String.valueOf(MENU1_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid))
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value(ErrorType.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("$.details").value("There is already a dish with this name"));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void createDuplicate() throws Exception {
        Dish invalid = new Dish(null, DISH1.getName(), 5000);
        mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                .param("menuId", String.valueOf(MENU1_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid))
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value(ErrorType.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("$.details").value("There is already a dish with this name"));
    }
}
