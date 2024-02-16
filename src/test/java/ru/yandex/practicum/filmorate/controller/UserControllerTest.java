package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    private static final String USERS_PATH = "/users";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationContext applicationContext;

    @AfterEach
    public void clear() {
        UserStorage userStorage = applicationContext.getBean(InMemoryUserStorage.class);
        userStorage.clear();
    }

    @Test
    public void testCreateUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(USERS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"email\":\"test@example.com\"," +
                                "\"login\":\"test\"," +
                                "\"name\":\"Test User\"," +
                                "\"birthday\":\"2004-03-25\"" +
                                "}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@example.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.login").value("test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test User"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthday").value("2004-03-25"));
    }

    @Test
    public void testCreateUserFailLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(USERS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"login\":\"dolore ullamco\"," +
                                "\"email\":\"yandex@mail.ru\"," +
                                "\"birthday\":\"2004-03-25\"" +
                                "}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testCreateUserFailEmail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(USERS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"dolore\"," +
                                "\"name\":\"\"," +
                                "\"email\":\"mail.ru\"," +
                                "\"birthday\":\"2004-03-25\"" +
                                "}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testCreateUserFailBirthday() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(USERS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"dolore\"," +
                                "\"name\":\"dolore\"," +
                                "\"email\":\"mail.ru\"," +
                                "\"birthday\":\"2034-03-25\"" +
                                "}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testCreateUserWithEmptyName() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(USERS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"login\":\"common\"," +
                                "\"email\":\"friend@common.ru\"," +
                                "\"birthday\":\"2000-08-20\"" +
                                "}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("friend@common.ru"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.login").value("common"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("common"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthday").value("2000-08-20"));
    }

    @Test
    public void testUpdateUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(USERS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"email\":\"test@example.com\"," +
                                "\"login\":\"test\"," +
                                "\"name\":\"Test User\"," +
                                "\"birthday\":\"2004-03-25\"" +
                                "}"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.put(USERS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"id\":1," +
                                "\"email\":\"test@example.com\"," +
                                "\"login\":\"test\"," +
                                "\"name\":\"User Update\"," +
                                "\"birthday\":\"2004-03-25\"" +
                                "}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@example.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.login").value("test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("User Update"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthday").value("2004-03-25"));
    }

    @Test
    public void testUpdateUnknownUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(USERS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"email\":\"test@example.com\"," +
                                "\"login\":\"test\"," +
                                "\"name\":\"Test User\"," +
                                "\"birthday\":\"2004-03-25\"" +
                                "}"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.put(USERS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"id\":3," +
                                "\"email\":\"test@example.com\"," +
                                "\"login\":\"test\"," +
                                "\"name\":\"User Update\"," +
                                "\"birthday\":\"2004-03-25\"" +
                                "}"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testGetUsers() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(USERS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"email\":\"test@example.comdfg\"," +
                                "\"login\":\"test\"," +
                                "\"name\":\"Test User\"," +
                                "\"birthday\":\"2004-03-25\"" +
                                "}"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get(USERS_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value("test@example.comdfg"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].login").value("test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Test User"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].birthday").value("2004-03-25"));
    }
}
