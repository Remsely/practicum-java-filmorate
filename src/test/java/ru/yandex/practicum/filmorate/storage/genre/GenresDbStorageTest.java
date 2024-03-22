package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MPADbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MPAStorage;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GenresDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private GenreStorage genreStorage;
    private FilmStorage filmStorage;

    @BeforeEach
    void init() {
        genreStorage = new GenreDbStorage(jdbcTemplate);
        MPAStorage mpaStorage = new MPADbStorage(jdbcTemplate);
        filmStorage = new FilmDbStorage(jdbcTemplate, genreStorage, mpaStorage);
    }

    @Test
    public void testGenreStorageNotEmpty() {
        List<Genre> genres = genreStorage.getAll();
        assertThat(genres).isNotNull();
        assertThat(genres.size()).isNotEqualTo(0);
    }

    @Test
    public void testGetFilmGenres() {
        Film film = Film.builder()
                .id(1L)
                .name("Film")
                .description("Description")
                .genres(Arrays.asList(
                        new Genre(1L, "Комедия"), new Genre(3L, "Мультфильм"))
                )
                .mpa(new MPA(1L, "G"))
                .releaseDate(LocalDate.of(2020, 8, 25))
                .duration(100)
                .likes(Collections.emptySet())
                .build();

        filmStorage.add(film);

        List<Genre> filmGenres = genreStorage.getFilmGenres(1);

        assertThat(filmGenres).isNotNull();
        assertThat(filmGenres.size()).isEqualTo(2);
        assertThat(filmGenres.get(0).getId()).isEqualTo(1L);
        assertThat(filmGenres.get(1).getId()).isEqualTo(3L);
    }

    @Test
    public void testAddFilmGenres() {
        Film film = Film.builder()
                .id(1L)
                .name("Film")
                .description("Description")
                .genres(Arrays.asList(
                        new Genre(1L, "Комедия"), new Genre(3L, "Мультфильм"))
                )
                .mpa(new MPA(1L, "G"))
                .releaseDate(LocalDate.of(2020, 8, 25))
                .duration(100)
                .likes(Collections.emptySet())
                .build();

        filmStorage.add(film);

        assertThat(genreStorage.getFilmGenres(1).size()).isEqualTo(2);

        genreStorage.addFilmGenres(1, Arrays.asList(
                new Genre(2L, "Драма"), new Genre(4L, "Триллер"))
        );

        assertThat(genreStorage.getFilmGenres(1).size()).isEqualTo(4);
    }

    @Test
    public void testNotContainMPA() {
        assertThat(genreStorage.notContainGenre(-1)).isEqualTo(true);
        assertThat(genreStorage.notContainGenre(1)).isEqualTo(false);
    }
}