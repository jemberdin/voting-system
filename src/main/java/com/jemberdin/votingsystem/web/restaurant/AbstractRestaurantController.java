package com.jemberdin.votingsystem.web.restaurant;

import com.jemberdin.votingsystem.model.Restaurant;
import com.jemberdin.votingsystem.service.RestaurantService;
import com.jemberdin.votingsystem.to.RestaurantTo;
import com.jemberdin.votingsystem.util.RestaurantUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.jemberdin.votingsystem.util.ValidationUtil.assureIdConsistent;
import static com.jemberdin.votingsystem.util.ValidationUtil.checkNew;

public abstract class AbstractRestaurantController {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private RestaurantService service;

    public AbstractRestaurantController(RestaurantService service) {
        this.service = service;
    }

    public List<Restaurant> getAll() {
        log.info("getAll restaurants");
        return service.getAll();
    }

    public Restaurant get(int id) {
        log.info("get restaurant {}", id);
        return service.get(id);
    }

    public Restaurant create(Restaurant restaurant) {
        checkNew(restaurant);
        log.info("create restaurant {}", restaurant);
        return service.create(restaurant);
    }

    public void delete(int id) {
        log.info("delete restaurant {}", id);
        service.delete(id);
    }

    public void update(Restaurant restaurant, int id) {
        assureIdConsistent(restaurant, id);
        log.info("update {} with id={}", restaurant, id);
        service.update(restaurant);
    }

    public Restaurant getByName(String name) {
        log.info("get restaurant by name {}", name);
        return service.getByName(name);
    }

    public List<RestaurantTo> getAllWithVotes() {
        log.info("getAll restaurants with votes");
        return RestaurantUtil.getTos(service.getAllWithVotes());
    }
}
