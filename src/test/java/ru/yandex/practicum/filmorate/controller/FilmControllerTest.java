package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

//Не знаю, насколько хорошие и правильные эти тесты. Решил, что глупо вручную писать валидацию для тестов, и загуглил,
// как тестить spring boot. Меня смущает, что эти тесты медленные. +- 300мс на каждый.
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmControllerTest {
    private static final String FILMS_PATH = "/films";

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCreateFilm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(FILMS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"name\":\"nisi eiusmod\"," +
                                "\"description\":\"adipisicing\"," +
                                "\"releaseDate\":\"1967-03-25\"," +
                                "\"duration\":100" +
                                "}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.releaseDate").value("1967-03-25"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("adipisicing"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("nisi eiusmod"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.duration").value(100));
    }

    @Test
    public void testCreateFilmFailName() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(FILMS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"name\":\"\"," +
                                "\"description\":\"Description\"," +
                                "\"releaseDate\":\"1900-03-25\"," +
                                "\"duration\":200" +
                                "}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testCreateFilmFailDescription() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(FILMS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"name\": \"Film name\"," +
                                "\"description\": \"Пятеро друзей ( комик-группа «Шарло»), приезжают в город " +
                                "Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им " +
                                "деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал " +
                                "кандидатом Коломбани.\"," +
                                "\"releaseDate\": \"1900-03-25\"," +
                                "\"duration\": 200" +
                                "}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testCreateFilmFailReleaseDate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(FILMS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"name\": \"Name\"," +
                                "\"description\": \"Description\"," +
                                "\"releaseDate\": \"1890-03-25\"," +
                                "\"duration\": 200" +
                                "}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testCreateFilmFailDuration() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(FILMS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"name\": \"Name\"," +
                                "\"description\": \"Descrition\"," +
                                "\"releaseDate\": \"1980-03-25\"," +
                                "\"duration\": -200" +
                                "}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testUpdateFilm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(FILMS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"name\":\"nisi eiusmod\"," +
                                "\"description\":\"adipisicing\"," +
                                "\"releaseDate\":\"1967-03-25\"," +
                                "\"duration\":100" +
                                "}"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.put(FILMS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"id\":1," +
                                "\"name\":\"Film Updated\"," +
                                "\"releaseDate\":\"1989-04-17\"," +
                                "\"description\":\"New film update decription\"," +
                                "\"duration\":190," +
                                "\"rate\":4" +
                                "}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.releaseDate").value("1989-04-17"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("New film update decription"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Film Updated"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.duration").value(190));
    }

    @Test
    public void testUpdateUnknownFilm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(FILMS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"name\":\"nisi eiusmod\"," +
                                "\"description\":\"adipisicing\"," +
                                "\"releaseDate\":\"1967-03-25\"," +
                                "\"duration\":100" +
                                "}"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.put(FILMS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"id\":35," +
                                "\"name\":\"Film Updated\"," +
                                "\"releaseDate\":\"1989-04-17\"," +
                                "\"description\":\"New film update decription\"," +
                                "\"duration\":190," +
                                "\"rate\":4" +
                                "}"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testGetFilms() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(FILMS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"name\":\"nisi eiusmod\"," +
                                "\"description\":\"adipisicing\"," +
                                "\"releaseDate\":\"1967-03-25\"," +
                                "\"duration\":100" +
                                "}"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get(FILMS_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].releaseDate").value("1967-03-25"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("adipisicing"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("nisi eiusmod"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].duration").value(100));
    }
}
