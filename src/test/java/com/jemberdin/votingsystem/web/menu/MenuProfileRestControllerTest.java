package com.jemberdin.votingsystem.web.menu;

import com.jemberdin.votingsystem.model.Menu;
import com.jemberdin.votingsystem.service.MenuService;
import com.jemberdin.votingsystem.util.MenuUtil;
import com.jemberdin.votingsystem.web.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.jemberdin.votingsystem.MenuTestData.MENU_TO_MATCHERS;
import static com.jemberdin.votingsystem.TestUtil.userHttpBasic;
import static com.jemberdin.votingsystem.UserTestData.USER1;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MenuProfileRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = MenuProfileRestController.REST_URL + '/';

    @Autowired
    private MenuService service;

    @Test
    void getDataForVoting() throws Exception {
        List<Menu> menus = service.getAllTodayWithRestaurantAndDishes();
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .with(userHttpBasic(USER1)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MENU_TO_MATCHERS.contentJson(MenuUtil.getTos(menus)));
    }
}
