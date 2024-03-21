package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.mpa.MPADbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MPAStorage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MPADbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private MPAStorage mpaStorage;

    @BeforeEach
    void init() {
        mpaStorage = new MPADbStorage(jdbcTemplate);
    }

    @Test
    public void testMPAStorageNotEmpty() {
        List<MPA> mpas = mpaStorage.getAll();
        assertThat(mpas).isNotNull();
        assertThat(mpas.size()).isNotEqualTo(0);
    }

    @Test
    public void testNotContainMPA() {
        assertThat(mpaStorage.notContainMPA(-1)).isEqualTo(true);
        assertThat(mpaStorage.notContainMPA(1)).isEqualTo(false);
    }
}