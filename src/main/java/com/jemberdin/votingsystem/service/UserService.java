package com.jemberdin.votingsystem.service;

import com.jemberdin.votingsystem.model.User;
import com.jemberdin.votingsystem.repository.UserRepository;
import com.jemberdin.votingsystem.to.UserTo;
import com.jemberdin.votingsystem.util.UserUtil;
import com.jemberdin.votingsystem.util.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

import static com.jemberdin.votingsystem.util.ValidationUtil.checkNotFound;
import static com.jemberdin.votingsystem.util.ValidationUtil.checkNotFoundWithId;

@Service
public class UserService {

    private final UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User create(User user) {
        Assert.notNull(user, "user must not be null");
        return repository.save(user);
    }

    public void delete(int id) throws NotFoundException {
        checkNotFoundWithId(repository.delete(id), id);
    }

    public User get(int id) throws NotFoundException {
        return checkNotFoundWithId(repository.get(id), id);
    }

    public User getByEmail(String email) throws NotFoundException {
        return checkNotFound(repository.getByEmail(email), "email=" + email);
    }

    public List<User> getAll() {
        return repository.getAll();
    }

    public void update(User user) throws NotFoundException {
        checkNotFoundWithId(repository.save(user), user.getId());
    }

    public void update(UserTo userTo) {
        User user = get(userTo.id());
        repository.save(UserUtil.updateFromTo(user, userTo));
    }
}
