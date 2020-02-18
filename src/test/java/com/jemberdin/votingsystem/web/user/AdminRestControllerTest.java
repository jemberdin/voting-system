package com.jemberdin.votingsystem.web.user;

import com.jemberdin.votingsystem.TestUtil;
import com.jemberdin.votingsystem.UserTestData;
import com.jemberdin.votingsystem.model.Role;
import com.jemberdin.votingsystem.model.User;
import com.jemberdin.votingsystem.service.UserService;
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

import static com.jemberdin.votingsystem.TestUtil.userHttpBasic;
import static com.jemberdin.votingsystem.UserTestData.*;
import static com.jemberdin.votingsystem.util.exception.ErrorType.VALIDATION_ERROR;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AdminRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = AdminRestController.REST_URL + '/';

    private UserService service;

    @Autowired
    public AdminRestControllerTest(UserService service) {
        this.service = service;
    }

    @Test
    void get() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + ADMIN_ID)
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_MATCHERS.contentJson(ADMIN));
    }

    @Test
    void getByEmail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "by?email=" + ADMIN.getEmail())
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_MATCHERS.contentJson(ADMIN));
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + USER1_ID)
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> service.get(USER1_ID));
    }

    @Test
    void getUnAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .with(userHttpBasic(USER1)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + 1)
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print());
    }

    @Test
    void update() throws Exception {
        User updated = UserTestData.getUpdated();
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + USER1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeAdditionProps(updated, "password", updated.getPassword()))
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isNoContent());

        USER_MATCHERS.assertMatch(service.get(USER1_ID), updated);
    }

    @Test
    void createWithLocation() throws Exception {
        User newUser = UserTestData.getNew();
        ResultActions action = mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeAdditionProps(newUser, "password", newUser.getPassword()))
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isCreated())
                .andDo(print());

        User created = TestUtil.readFromJson(action, User.class);
        Integer newId = created.getId();
        newUser.setId(newId);
        USER_MATCHERS.assertMatch(created, newUser);
        USER_MATCHERS.assertMatch(service.get(newId), newUser);
    }

    @Test
    void getAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_MATCHERS.contentJson(ADMIN, USER1, USER2));
    }

    @Test
    void enable() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch(REST_URL + USER1_ID)
                .param("enabled", "false")
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertFalse(service.get(USER1_ID).isEnabled());
    }

    @Test
    void createInvalid() throws Exception {
        User expected = new User(null, null, "", "newPass", Role.ROLE_USER, Role.ROLE_ADMIN);
        mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(expected))
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value(VALIDATION_ERROR.name()))
                .andDo(print());
    }

    @Test
    void updateInvalid() throws Exception {
        User updated = new User(USER1);
        updated.setName("");
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + USER1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeAdditionProps(updated, "password", updated.getPassword()))
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(jsonPath("$.type").value(VALIDATION_ERROR.name()));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void updateDuplicate() throws Exception {
        User updated = new User(USER1);
        updated.setEmail("admin@gmail.com");
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + USER1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeAdditionProps(updated, "password", updated.getPassword()))
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value(VALIDATION_ERROR.name()))
                .andExpect(jsonPath("$.details").value("There is already a user with this email address"))
                .andDo(print());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void createDuplicate() throws Exception {
        User expected = new User(null, "New", "user1@gmail.com", "newPass", Role.ROLE_USER, Role.ROLE_ADMIN);
        mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeAdditionProps(expected, "password", expected.getPassword()))
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value(VALIDATION_ERROR.name()))
                .andExpect(jsonPath("$.details").value("There is already a user with this email address"))
                .andDo(print());
    }
}
