package com.jemberdin.votingsystem.util;

import com.jemberdin.votingsystem.model.Menu;
import com.jemberdin.votingsystem.to.MenuTo;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MenuUtil {

    public static List<MenuTo> getTos(Collection<Menu> menus) {
        return menus.stream().map(MenuUtil::createTo).collect(Collectors.toList());
    }

    public static MenuTo createTo(Menu menu) {
        return new MenuTo(menu.getRestaurant().getId(), menu.getRestaurant().getName(), menu.getDate(), DishUtil.getTos(menu.getDishes()));
    }
}
