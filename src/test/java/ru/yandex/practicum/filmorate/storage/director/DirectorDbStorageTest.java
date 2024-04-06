package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DirectorDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private DirectorDbStorage directorStorage;

    @BeforeEach
    void init() {
        directorStorage = new DirectorDbStorage(jdbcTemplate);
    }

    @Test
    public void testAddDirector() {
        assertThatCode(() -> {
            Director director = Director.builder()
                    .id(1L)
                    .name("Mega Famous Director 1")
                    .build();
            directorStorage.add(director);
        }).doesNotThrowAnyException();
    }

    @Test
    public void testUpdateDirector() {
        Director director = Director.builder()
                .id(1L)
                .name("Mega Famous Director 1")
                .build();
        directorStorage.add(director);
        directorStorage.add(director);

        Director updateDirector = Director.builder()
                .id(1L)
                .name("Mega Famous Director 1")
                .build();
        directorStorage.add(director);
        directorStorage.update(updateDirector);

        Director saveDirector = directorStorage.get(1);
        assertThat(saveDirector)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(updateDirector);
    }

    @Test
    public void testGetDirector() {
        Director director = Director.builder()
                .id(1L)
                .name("Mega Famous Director 1")
                .build();
        directorStorage.add(director);

        Director saveDirector = directorStorage.get(1);
        assertThat(saveDirector)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(director);
    }

    @Test
    public void testGetAllDirectors() {
        Director director1 = Director.builder()
                .id(1L)
                .name("Mega Famous Director 1")
                .build();

        Director director2 = Director.builder()
                .id(2L)
                .name("Mega Famous Director 2")
                .build();

        directorStorage.add(director1);
        directorStorage.add(director2);

        List<Director> directors = directorStorage.getAll();
        assertThat(directors).isNotNull();
        assertThat(directors.size()).isEqualTo(2);
        assertThat(directors.get(0)).isEqualTo(director1);
        assertThat(directors.get(1)).isEqualTo(director2);
    }

    @Test
    public void testDeleteDirector() {
        Director director = Director.builder()
                .id(1L)
                .name("Mega Famous Director 1")
                .build();
        directorStorage.add(director);

        Director savedDirector = directorStorage.get(1);
        assertThat(savedDirector)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(director);

        directorStorage.delete(1);

        List<Director> directors = directorStorage.getAll();
        assertThat(directors)
                .isEmpty();
    }

    @Test
    public void testGetDirectorsWithName() {
        Director director = Director.builder()
                .id(1L)
                .name("Mega Famous Director 1")
                .build();
        Director director2 = Director.builder()
                .id(2L)
                .name("Famous Director 2")
                .build();
        directorStorage.add(director);
        directorStorage.add(director2);
        List<Director> expList = new ArrayList<>();
        expList.add(director);
        List<Director> directorsList = directorStorage.getDirectorsWithName("Mega");
        assertThat(directorsList)
                .isNotEmpty()
                .isNotNull()
                .isEqualTo(expList);
    }
}
