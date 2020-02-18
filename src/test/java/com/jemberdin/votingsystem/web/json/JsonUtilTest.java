package com.jemberdin.votingsystem.web.json;

import com.jemberdin.votingsystem.UserTestData;
import com.jemberdin.votingsystem.model.Menu;
import com.jemberdin.votingsystem.model.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.jemberdin.votingsystem.MenuTestData.*;
import static com.jemberdin.votingsystem.MenuTestData.MENU4;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonUtilTest {

    @Test
    void readWriteValue() throws Exception {
        String json = JsonUtil.writeValue(MENU1);
        System.out.println(json);
        Menu menu = JsonUtil.readValue(json, Menu.class);
        MENU_MATCHERS.assertMatch(menu, MENU1);
    }

    @Test
    void readWriteValues() throws Exception {
        String json = JsonUtil.writeValue(List.of(MENU3, MENU1, MENU4));
        System.out.println(json);
        List<Menu> menus = JsonUtil.readValues(json, Menu.class);
        MENU_MATCHERS.assertMatch(menus, List.of(MENU3, MENU1, MENU4));
    }

    @Test
    void writeOnlyAccess() throws Exception {
        String json = JsonUtil.writeValue(UserTestData.USER1);
        System.out.println(json);
        assertThat(json, not(containsString("password")));
        String jsonWithPass = JsonUtil.writeAdditionProps(UserTestData.USER1, "password", "newPassword");
        System.out.println(jsonWithPass);
        User user = JsonUtil.readValue(jsonWithPass, User.class);
        assertEquals(user.getPassword(), "newPassword");
    }
}
