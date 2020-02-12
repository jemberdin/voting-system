package com.jemberdin.votingsystem.web.vote;

import com.jemberdin.votingsystem.model.Vote;
import com.jemberdin.votingsystem.service.VoteService;
import com.jemberdin.votingsystem.web.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = VoteRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class VoteRestController {

    static final String REST_URL = "/rest/votes";

    private final Logger log = LoggerFactory.getLogger(getClass());

    private VoteService service;

    @Autowired
    public VoteRestController(VoteService service) {
        this.service = service;
    }

    @GetMapping
    public List<Vote> getAll() {
        int userId = SecurityUtil.authUserId();
        log.info("getAll for user {}", userId);
        return service.getAllWithRestaurant(userId);
    }

    @GetMapping("/{id}")
    public Vote get(@PathVariable int id) {
        int userId = SecurityUtil.authUserId();
        log.info("get vote {} for user {}", id, userId);
        return service.getWithRestaurant(id, userId);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Vote> createWithLocation(@RequestParam int restaurantId) {
        int userId = SecurityUtil.authUserId();
        log.info("create vote for user {} for restaurant {}", userId, restaurantId);
        Vote created = service.create(userId, restaurantId);

        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable int id, @RequestParam int restaurantId) {
        int userId = SecurityUtil.authUserId();
        log.info("update vote for user {}", userId);
        service.update(id, userId, restaurantId);
    }
}
