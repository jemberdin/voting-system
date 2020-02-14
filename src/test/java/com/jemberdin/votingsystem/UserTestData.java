package com.jemberdin.votingsystem;

import com.jemberdin.votingsystem.model.Role;
import com.jemberdin.votingsystem.model.User;

import java.util.Collections;
import java.util.Date;

import static com.jemberdin.votingsystem.model.AbstractBaseEntity.START_SEQ;

public class UserTestData {

    public static final int USER1_ID = START_SEQ;
    public static final int ADMIN_ID = START_SEQ + 2;

    public static final User USER1 = new User(USER1_ID, "User1", "user1@gmail.com", "password", Role.ROLE_USER);
    public static final User USER2 = new User(USER1_ID + 1, "User2", "user2@gmail.com", "password", Role.ROLE_USER);
    public static final User ADMIN = new User(ADMIN_ID, "Admin", "admin@gmail.com", "admin", Role.ROLE_ADMIN, Role.ROLE_USER);

    public static User getNew() {
        return new User(null, "New", "new@gmail.com", "newPass", false, new Date(), Collections.singleton(Role.ROLE_USER));
    }

    public static User getUpdated() {
        User updated = new User(USER1);
        updated.setName("UpdatedName");
        updated.setRoles(Collections.singletonList(Role.ROLE_ADMIN));
        return updated;
    }

    public static TestMatchers<User> USER_MATCHERS = TestMatchers.useFieldsComparator(User.class, "registered", "password");
}
