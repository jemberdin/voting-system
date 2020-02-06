package com.jemberdin.votingsystem.web.menu;

import com.jemberdin.votingsystem.model.Menu;
import com.jemberdin.votingsystem.service.MenuService;
import com.jemberdin.votingsystem.to.MenuTo;
import com.jemberdin.votingsystem.util.MenuUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

import static com.jemberdin.votingsystem.util.ValidationUtil.assureIdConsistent;
import static com.jemberdin.votingsystem.util.ValidationUtil.checkNew;

public abstract class AbstractMenuController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private MenuService service;

    public AbstractMenuController(MenuService service) {
        this.service = service;
    }

    public List<Menu> getAll(int restaurantId) {
        log.info("getAll for restaurant {}", restaurantId);
        return service.getAll(restaurantId);
    }

    public Menu get(int id, int restaurantId) {
        log.info("get menu {} for restaurant {}", id, restaurantId);
        return service.get(id, restaurantId);
    }

    public Menu create(Menu menu, int restaurantId) {
        checkNew(menu);
        log.info("create {} for restaurant {}", menu, restaurantId);
        return service.create(menu, restaurantId);
    }

    public void delete(int id, int restaurantId) {
        log.info("delete menu {} for restaurant {}", id, restaurantId);
        service.delete(id, restaurantId);
    }

    public void update(Menu menu, int id, int restaurantId) {
        assureIdConsistent(menu, id);
        log.info("update {} for restaurant {}", menu, restaurantId);
        service.update(menu, restaurantId);
    }

    public Menu getByDate(int restaurantId, LocalDate date) {
        log.info("get menu by date {} for restaurant {} ", date, restaurantId);
        return service.getByDate(restaurantId, date);
    }

    public List<MenuTo> getAllTodayWithRestaurantAndDishes() {
        log.info("get all for voting");
        return MenuUtil.getTos(service.getAllTodayWithRestaurantAndDishes());
    }
}
