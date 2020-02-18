package com.jemberdin.votingsystem.web.menu;

import com.jemberdin.votingsystem.MenuTestData;
import com.jemberdin.votingsystem.model.Menu;
import com.jemberdin.votingsystem.service.MenuService;
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

import java.time.LocalDate;

import static com.jemberdin.votingsystem.MenuTestData.*;
import static com.jemberdin.votingsystem.RestaurantTestData.RESTAURANT1_ID;
import static com.jemberdin.votingsystem.TestUtil.*;
import static com.jemberdin.votingsystem.UserTestData.ADMIN;
import static com.jemberdin.votingsystem.UserTestData.USER1;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class MenuAdminRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = MenuAdminRestController.REST_URL + '/';

    private MenuService service;

    @Autowired
    public MenuAdminRestControllerTest(MenuService service) {
        this.service = service;
    }

    @Test
    void getAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param("restaurantId", String.valueOf(RESTAURANT1_ID))
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MENU_MATCHERS.contentJson(MENU3, MENU1, MENU4));
    }

    @Test
    void get() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + MENU1_ID)
                .param("restaurantId", String.valueOf(RESTAURANT1_ID))
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> MENU_MATCHERS.assertMatch(readFromJsonMvcResult(result, Menu.class), MENU1));
    }

    @Test
    void getNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + MENU2_ID)
                .param("restaurantId", String.valueOf(RESTAURANT1_ID))
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void getByDate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "by")
                .param("restaurantId", String.valueOf(RESTAURANT1_ID))
                .param("date", String.valueOf(MENU1.getDate()))
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(result -> MENU_MATCHERS.assertMatch(readFromJsonMvcResult(result, Menu.class), MENU1));
    }

    @Test
    void getUnAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + MENU1_ID)
                .param("restaurantId", String.valueOf(RESTAURANT1_ID)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param("restaurantId", String.valueOf(RESTAURANT1_ID))
                .with(userHttpBasic(USER1)))
                .andExpect(status().isForbidden());
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + MENU1_ID)
                .param("restaurantId", String.valueOf(RESTAURANT1_ID))
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isNoContent())
                .andDo(print());
        assertThrows(NotFoundException.class, () -> service.get(MENU1_ID, RESTAURANT1_ID));
    }

    @Test
    void deleteNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + MENU2_ID)
                .param("restaurantId", String.valueOf(RESTAURANT1_ID))
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void createWithLocation() throws Exception {
        Menu newMenu = MenuTestData.getNew();
        ResultActions action = mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                .param("restaurantId", String.valueOf(RESTAURANT1_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newMenu))
                .with(userHttpBasic(ADMIN)))
                .andDo(print());

        Menu created = readFromJson(action, Menu.class);
        Integer newId = created.getId();
        newMenu.setId(newId);
        MENU_MATCHERS.assertMatch(created, newMenu);
        MENU_MATCHERS.assertMatch(service.get(newId, RESTAURANT1_ID), newMenu);
    }

    @Test
    void update() throws Exception {
        Menu updated = MenuTestData.getUpdated();

        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + MENU1_ID)
                .param("restaurantId", String.valueOf(RESTAURANT1_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated))
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isNoContent())
                .andDo(print());
        MENU_MATCHERS.assertMatch(service.get(MENU1_ID, RESTAURANT1_ID), updated);
    }

    @Test
    void updatePastDateError() throws Exception {
        Menu updated = MenuTestData.getUpdated();
        updated.setDate(LocalDate.now().minusDays(2));

        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + MENU1_ID)
                .param("restaurantId", String.valueOf(RESTAURANT1_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated))
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value(ErrorType.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("$.details").value("com.jemberdin.votingsystem.util.exception.CreateMenuForPastDateException: Must be at least today's date"));
    }

    @Test
    void createInvalid() throws Exception {
        Menu invalid = new Menu(null, null);
        mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                .param("restaurantId", String.valueOf(RESTAURANT1_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid))
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value(ErrorType.VALIDATION_ERROR.name()));
    }

    @Test
    void updateInvalid() throws Exception {
        Menu invalid = new Menu(MENU1_ID, null);
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + MENU1_ID)
                .param("restaurantId", String.valueOf(RESTAURANT1_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid))
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value(ErrorType.VALIDATION_ERROR.name()));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void createDuplicate() throws Exception {
        Menu invalid = new Menu(null, MENU1.getDate());
        mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                .param("restaurantId", String.valueOf(RESTAURANT1_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid))
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value(ErrorType.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("$.details").value("There is already a menu with this date"));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void updateDuplicate() throws Exception {
        Menu invalid = new Menu(MENU1_ID, MENU3.getDate());

        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + MENU1_ID)
                .param("restaurantId", String.valueOf(RESTAURANT1_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid))
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value(ErrorType.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("$.details").value("There is already a menu with this date"));
    }
}
