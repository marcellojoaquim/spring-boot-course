package com.projectspring.course.repositories;

import com.projectspring.course.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    UserRepository repository;

    User user;

    @BeforeEach
    void setUp() {
        user = new User(
                null,
                "Maria Brown",
                "maria@gmail.com",
                "988888888",
                "123456");
    }

    @Test
    void saveUser(){
        User actual = repository.save(user);

        assertNotNull(actual.getId());
        assertTrue(actual.getId() > 0);
        assertEquals("Maria Brown", actual.getName());
        assertEquals("123456", actual.getPassword());
    }

    @Test
    void findUserById(){
        repository.save(user);
        User actual = repository.findById(user.getId()).get();

        assertNotNull(actual.getId());
        assertTrue(actual.getId() > 0);
        assertEquals(user.getId(), actual.getId());
    }

    @Test
    void findAllUsers(){
        User user1 = new User(
                null,
                "Alex Green",
                "alex@gmail.com",
                "977777777",
                "123456");

        repository.save(user);
        repository.save(user1);

        List<User> userList = repository.findAll();

        assertNotNull(userList);
        assertEquals(2, userList.size());
    }

    @Test
    void deleteUserById(){
        repository.save(user);
        repository.deleteById(user.getId());

        Optional<User> actual = repository.findById(user.getId());

        assertTrue(actual.isEmpty());
    }

}