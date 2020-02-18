package com.jemberdin.votingsystem.web.user;

import com.jemberdin.votingsystem.model.User;
import com.jemberdin.votingsystem.service.UserService;
import com.jemberdin.votingsystem.to.UserTo;
import com.jemberdin.votingsystem.util.UserUtil;
import com.jemberdin.votingsystem.util.exception.ErrorType;
import com.jemberdin.votingsystem.web.AbstractControllerTest;
import com.jemberdin.votingsystem.web.json.JsonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.jemberdin.votingsystem.TestUtil.readFromJson;
import static com.jemberdin.votingsystem.TestUtil.userHttpBasic;
import static com.jemberdin.votingsystem.UserTestData.*;
import static com.jemberdin.votingsystem.web.user.ProfileRestController.REST_URL;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProfileRestControllerTest extends AbstractControllerTest {

    private UserService userService;

    @Autowired
    public ProfileRestControllerTest(UserService userService) {
        this.userService = userService;
    }

    @Test
    void get() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .with(userHttpBasic(USER1)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_MATCHERS.contentJson(USER1));
    }

    @Test
    void getUnAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL)
                .with(userHttpBasic(USER1)))
                .andExpect(status().isNoContent());
        USER_MATCHERS.assertMatch(userService.getAll(), ADMIN, USER2);
    }

    @Test
    void update() throws Exception {
        UserTo updatedTo = new UserTo(null, "updatedName", "updatedemail@gmail.com", "updatedPassword");
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo))
                .with(userHttpBasic(USER1)))
                .andDo(print())
                .andExpect(status().isNoContent());
        USER_MATCHERS.assertMatch(userService.get(USER1_ID), UserUtil.updateFromTo(new User(USER1), updatedTo));
    }

    @Test
    void register() throws Exception {
        UserTo newTo = new UserTo(null, "newName", "newemail@gmail.com", "newPassword");
        User newUser = UserUtil.createNewFromTo(newTo);
        ResultActions action = mockMvc.perform(MockMvcRequestBuilders.post(REST_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newTo)))
                .andDo(print())
                .andExpect(status().isCreated());

        User created = readFromJson(action, User.class);
        Integer newId = created.getId();
        newUser.setId(newId);
        USER_MATCHERS.assertMatch(created, newUser);
        USER_MATCHERS.assertMatch(userService.get(newId), newUser);
    }

    @Test
    void updateInvalid() throws Exception {
        UserTo updatedTo = new UserTo(null, null, "password", null);

        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo))
                .with(userHttpBasic(USER1)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value(ErrorType.VALIDATION_ERROR.name()))
                .andDo(print());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void updateDuplicate() throws Exception {
        UserTo updatedTo = new UserTo(null, "updatedName", "admin@gmail.com", "updatedPassword");
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo))
                .with(userHttpBasic(USER1)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value(ErrorType.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("$.details").value("There is already a user with this email address"))
                .andDo(print());
    }
}
