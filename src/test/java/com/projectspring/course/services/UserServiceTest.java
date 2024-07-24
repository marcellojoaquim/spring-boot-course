package com.projectspring.course.services;

import com.projectspring.course.entities.User;
import com.projectspring.course.repositories.UserRepository;
import com.projectspring.course.services.exceptions.DatabaseException;
import com.projectspring.course.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;


import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private User u1;

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;

    @BeforeEach
    void setUp() {
        u1 = new User(
                null,
                "Maria Brown",
                "maria@gmail.com",
                "988888888",
                "123456");
    }

    @Test
    void testFindAllUsers() {
        User u2 = new User(null, "Alex Green", "alex@gmail.com", "977777777", "123456");

        given(repository.findAll()).willReturn(List.of(u1, u2));
        List<User> userList = service.findAll();

        assertNotNull(userList);
        assertEquals(2, userList.size());
        assertEquals("Alex Green", userList.get(1).getName());
        assertEquals("alex@gmail.com", userList.get(1).getEmail());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testFindAllUsersEmptyList() {
        given(repository.findAll()).willReturn(Collections.emptyList());
        List<User> userList = service.findAll();

        assertTrue(userList.isEmpty());
    }

    @Test
    void testFindUserById() {
        given(repository.findById(anyLong())).willReturn(Optional.of(u1));
        User actual = service.findById(1L);

        assertNotNull(actual);
        assertEquals("Maria Brown", actual.getName());
    }

    @Test
    void testUserFindByIdNotFounded() {
        var expectedMessage = "Resource not found. Id ";
        u1.setId(1L);

        given(repository.findById(anyLong())).willReturn(Optional.empty());

        ResourceNotFoundException actual = assertThrows(ResourceNotFoundException.class,
                () -> {
                    service.findById(1L);
                });

        assertEquals(expectedMessage + u1.getId(), actual.getMessage());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void testInsertUser() {
        given(repository.save(u1)).willReturn(u1);

        User actual = service.insert(u1);

        assertNotNull(actual);
        assertEquals("Maria Brown", actual.getName());
        assertEquals("maria@gmail.com", actual.getEmail());
        verify(repository, times(1)).save(actual);
    }

    @Test
    void testDeleteUser() {
        u1.setId(1L);
        given(repository.existsById(anyLong())).willReturn(true);
        willDoNothing().given(repository).deleteById(u1.getId());

        service.delete(u1.getId());
        verify(repository, times(1)).deleteById(anyLong());
    }

    @Test
    void testDeleteUserResourceNotFoundException() {
        var expectedMessage = "Resource not found. Id ";
        u1.setId(1L);

        given(repository.existsById(anyLong())).willReturn(false);
        ResourceNotFoundException actual = assertThrows(ResourceNotFoundException.class,
                () -> {
                    service.delete(u1.getId());
                });

        assertEquals(expectedMessage + u1.getId(), actual.getMessage());
        verify(repository, times(1)).existsById(1L);
        verify(repository, times(0)).deleteById(1L);
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setId(1L);
        user.setName(u1.getName());
        user.setEmail(u1.getEmail());
        user.setPassword(u1.getPassword());
        user.setPhone(u1.getPhone());

        given(repository.getReferenceById(anyLong())).willReturn(u1);
        given(repository.save(any(User.class))).willReturn(user);

        User actual = service.update(1L, u1);

        assertNotNull(user.getId());
        assertEquals("Maria Brown", user.getName());
        assertEquals("maria@gmail.com", user.getEmail());
        verify(repository, times(1)).getReferenceById(anyLong());
        verify(repository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUserResourceNotFoundException() {
        var expectedMessage = "Resource not found. Id ";
        u1.setId(1L);

        given(repository.getReferenceById(anyLong())).willThrow(EntityNotFoundException.class);

        ResourceNotFoundException actual = assertThrows(ResourceNotFoundException.class,
                () -> {
                    service.update(1L, u1);
                });

        assertEquals(expectedMessage + u1.getId(), actual.getMessage());
    }

    @Test
    public void testDeleteUser_DataIntegrityViolationException() {
        u1.setId(3L);

        given(repository.existsById(u1.getId())).willReturn(true);
        doThrow(DataIntegrityViolationException.class).when(repository).deleteById(u1.getId());

        assertThrows(DatabaseException.class, () -> service.delete(u1.getId()));

        verify(repository, times(1)).existsById(u1.getId());
    }
}