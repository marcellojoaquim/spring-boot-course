package com.projectspring.course.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectspring.course.entities.User;
import com.projectspring.course.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Arrays;
import java.util.List;


import static org.hamcrest.CoreMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserResource.class)
class UserResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    UserService service;

    private User user;
    private User user1;

    @BeforeEach
    void setUp() {
        user = new User(
                1L,
                "Maria Brown",
                "maria@gmail.com",
                "988888888",
                "123456");
    }

    @Test
    void testFindAllUsers() throws Exception {
        user1 = new User(
                2L,
                "Alex Green",
                "alex@gmail.com",
                "977777777",
                "123456");

        List<User> userList = Arrays.asList(user, user1);
        given(service.findAll()).willReturn(userList);

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Maria Brown"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Alex Green"))
                .andExpect((ResultMatcher) jsonPath("$.size()", is(userList.size())));

        verify(service, times(1)).findAll();
    }

    @Test
    void testFindUserById() throws Exception {
        long id = 1L;
        given(service.findById(id)).willReturn(user);

        mockMvc.perform(get("/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Maria Brown"))
                .andExpect(jsonPath("$.email").value("maria@gmail.com"));
        verify(service, times(1)).findById(id);
    }

    @Test
    void testInsertUser() throws Exception {
        given(service.insert(any(User.class))).willAnswer((invocation) -> invocation.getArgument(0));

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isCreated());

        verify(service, times(1)).insert(user);

    }

    @Test
    void testDeleteUser() throws Exception {
        long id = 123L;
        willDoNothing().given(service).delete(id);

        mockMvc.perform(delete("/users/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void testUpdateUser() throws Exception{
        long id = 1L;
        User updateUser = new User(
                1L,
                "Maria Brown",
                "maria@gmail.com",
                "988888888",
                "123456");

        given(service.findById(id)).willReturn(user);
        given(service.update(id, user)).willReturn(updateUser);

        ResultActions response = mockMvc.perform(put("/users/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updateUser)));

        response.andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.name", is(updateUser.getName())))
                .andExpect((ResultMatcher) jsonPath("$.email", is(updateUser.getEmail())));
    }
}