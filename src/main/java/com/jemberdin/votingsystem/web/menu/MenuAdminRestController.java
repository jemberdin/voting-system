package com.jemberdin.votingsystem.web.menu;

import com.jemberdin.votingsystem.model.Menu;
import com.jemberdin.votingsystem.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = MenuAdminRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class MenuAdminRestController extends AbstractMenuController {

    static final String REST_URL = "/rest/admin/menus";

    @Autowired
    public MenuAdminRestController(MenuService service) {
        super(service);
    }

    @Override
    @GetMapping
    public List<Menu> getAll(@RequestParam int restaurantId) {
        return super.getAll(restaurantId);
    }

    @Override
    @GetMapping("/{id}")
    public Menu get(@PathVariable int id, @RequestParam int restaurantId) {
        return super.get(id, restaurantId);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Menu> createWithLocation(@RequestBody Menu menu, @RequestParam int restaurantId) {
        Menu created = super.create(menu, restaurantId);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL)
                .queryParam("restaurantId", restaurantId)
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id, @RequestParam int restaurantId) {
        super.delete(id, restaurantId);
    }

    @Override
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@RequestBody Menu menu, @PathVariable int id, @RequestParam int restaurantId) {
        super.update(menu, id, restaurantId);
    }

    @Override
    @GetMapping("/by")
    public Menu getByDate(@RequestParam int restaurantId, @RequestParam @NonNull LocalDate date) {
        return super.getByDate(restaurantId, date);
    }
}
