package com.jemberdin.votingsystem.web.restaurant;

import com.jemberdin.votingsystem.model.Restaurant;
import com.jemberdin.votingsystem.to.RestaurantTo;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = RestaurantProfileRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class RestaurantProfileRestController extends AbstractRestaurantController {

    static final String REST_URL = "/rest/restaurants";

    @GetMapping
    public List<Restaurant> getAll() {
        return super.getAll();
    }

    @Override
    @GetMapping("/{id}")
    public Restaurant get(@PathVariable int id) {
        return super.get(id);
    }

    @Override
    @GetMapping("/by")
    public Restaurant getByName(@RequestParam String name) {
        return super.getByName(name);
    }

    @Override
    @GetMapping("/result")
    public List<RestaurantTo> getAllWithVotes() {
        return super.getAllWithVotes();
    }
}
