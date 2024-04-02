package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Director;

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
                    .id(10L)
                    .name("DirName")
                    .build();
            directorStorage.add(director);
        }).doesNotThrowAnyException();
    }
}
