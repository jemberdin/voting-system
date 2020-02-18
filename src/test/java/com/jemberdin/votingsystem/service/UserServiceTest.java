package com.jemberdin.votingsystem.service;

import com.jemberdin.votingsystem.model.Role;
import com.jemberdin.votingsystem.model.User;
import com.jemberdin.votingsystem.util.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static com.jemberdin.votingsystem.UserTestData.*;

public class UserServiceTest extends AbstractServiceTest {

    protected UserService service;

    @Autowired
    public UserServiceTest(UserService service) {
        this.service = service;
    }

    @Test
    void create() throws Exception {
        User newUser = getNew();
        User created = service.create(new User(newUser));
        Integer newId = created.getId();
        newUser.setId(newId);
        USER_MATCHERS.assertMatch(created, newUser);
        USER_MATCHERS.assertMatch(service.get(newId), newUser);
    }

    @Test
    void duplicateMailCreate() throws Exception {
        assertThrows(DataAccessException.class, () ->
                service.create(new User(null, "Duplicate", "user1@gmail.com", "newPass", Role.ROLE_USER)));
    }

    @Test
    void delete() throws Exception {
        service.delete(USER1_ID);
        assertThrows(NotFoundException.class, () ->
                service.delete(USER1_ID));
    }

    @Test
    void deletedNotFound() throws Exception {
        assertThrows(NotFoundException.class, () ->
                service.delete(1));
    }

    @Test
    void get() throws Exception {
        User user = service.get(ADMIN_ID);
        USER_MATCHERS.assertMatch(user, ADMIN);
    }

    @Test
    void getNotFound() throws Exception {
        assertThrows(NotFoundException.class, () ->
                service.get(1));
    }

    @Test
    void getByEmail() throws Exception {
        User user = service.getByEmail("admin@gmail.com");
        USER_MATCHERS.assertMatch(user, ADMIN);
    }

    @Test
    void update() throws Exception {
        User updated = getUpdated();
        service.update(new User(updated));
        USER_MATCHERS.assertMatch(service.get(USER1_ID), updated);
    }

    @Test
    void getAll() throws Exception {
        List<User> all = service.getAll();
        USER_MATCHERS.assertMatch(all, ADMIN, USER1, USER2);
    }

    @Test
    void createWithException() throws Exception {
        validateRootCause(() -> service.create(new User(null, "  ", "mail@gmail.com", "password", Role.ROLE_USER)), ConstraintViolationException.class);
        validateRootCause(() -> service.create(new User(null, "User", "  ", "password", Role.ROLE_USER)), ConstraintViolationException.class);
        validateRootCause(() -> service.create(new User(null, "User", "mail@gmail.com", "  ", Role.ROLE_USER)), ConstraintViolationException.class);
    }

    @Test
    void enable() {
        service.enable(USER1_ID, false);
        assertFalse(service.get(USER1_ID).isEnabled());
        service.enable(USER1_ID, true);
        assertTrue(service.get(USER1_ID).isEnabled());
    }
}
