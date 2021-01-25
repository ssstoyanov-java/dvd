package io.ssstoyanov.dvd2.controllers;

import com.google.gson.Gson;
import io.ssstoyanov.dvd2.entities.User;
import io.ssstoyanov.dvd2.repositories.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @BeforeTestClass
    public void createNewUser(){
        userRepository.save(new User(new ObjectId(), "root", "df39f196e2b87e0a6c2e8184c6633e6e", null));
    }

    @BeforeEach
    public void cleanUsersBefore() {
        userRepository.deleteUserByUsername("test");
    }

    @Test
    public void userCreation() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .content(new Gson()
                        .toJson(new User(new ObjectId(), "test", "test", null)))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Assertions.assertEquals(result.getResponse().getStatus(), 200);
    }

    @AfterEach
    public void cleanUsersAfter() {
        userRepository.deleteUserByUsername("test");
    }

    @Test
    public void userCreationWithDuplication() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .content(new Gson()
                        .toJson(new User(new ObjectId(), "root", "test", null)))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andReturn();
        Assertions.assertEquals(result.getResponse().getStatus(), 409);
    }

    @Test
    public void userCreationWithNull() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .content(new Gson()
                        .toJson(new User(new ObjectId(), null, null, null)))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        Assertions.assertEquals(result.getResponse().getStatus(), 400);
    }

}
