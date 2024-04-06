package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MPADbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MPAStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private FilmStorage filmStorage;
    private UserStorage userStorage;

    @BeforeEach
    void init() {
        GenreStorage genreStorage = new GenreDbStorage(jdbcTemplate);
        MPAStorage mpaStorage = new MPADbStorage(jdbcTemplate);
        DirectorStorage directorStorage = new DirectorDbStorage(jdbcTemplate);
        filmStorage = new FilmDbStorage(jdbcTemplate, genreStorage, mpaStorage, directorStorage);
        userStorage = new UserDbStorage(jdbcTemplate);
    }

    @Test
    public void testAddUser() {
        assertThatCode(() -> {
            Film film = Film.builder()
                    .id(1L)
                    .name("Film")
                    .description("Description")
                    .genres(Collections.emptyList())
                    .directors(Collections.emptyList())
                    .mpa(new MPA(1L, "G"))
                    .releaseDate(LocalDate.of(2020, 8, 25))
                    .duration(100)
                    .likes(Collections.emptySet())
                    .build();
            filmStorage.add(film);
        }).doesNotThrowAnyException();
    }

    @Test
    public void testUpdateFilm() {
        Film film = Film.builder()
                .id(1L)
                .name("Film")
                .description("Description")
                .genres(Collections.emptyList())
                .directors(Collections.emptyList())
                .mpa(new MPA(1L, "G"))
                .releaseDate(LocalDate.of(2020, 8, 25))
                .duration(100)
                .likes(Collections.emptySet())
                .build();
        filmStorage.add(film);

        filmStorage.add(film);

        Film updatedFilm = Film.builder()
                .id(1L)
                .name("Filmmmmmm")
                .description("Descriptionnnnnnnn")
                .genres(Collections.emptyList())
                .directors(Collections.emptyList())
                .mpa(new MPA(1L, "G"))
                .releaseDate(LocalDate.of(2020, 8, 25))
                .duration(100)
                .likes(Collections.emptySet())
                .build();
        filmStorage.add(film);

        filmStorage.update(updatedFilm);

        Film savedFilm = filmStorage.get(1);

        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(updatedFilm);
    }

    @Test
    public void testGetFilm() {
        Film film = Film.builder()
                .id(1L)
                .name("Film")
                .description("Description")
                .genres(Collections.emptyList())
                .directors(Collections.emptyList())
                .directors(Collections.emptyList())
                .mpa(new MPA(1L, "G"))
                .releaseDate(LocalDate.of(2020, 8, 25))
                .duration(100)
                .likes(Collections.emptySet())
                .build();

        filmStorage.add(film);

        Film savedFilm = filmStorage.get(1);

        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
    public void testGetAllFilms() {
        Film film1 = Film.builder()
                .id(1L)
                .name("Film1")
                .description("Description1")
                .genres(Collections.emptyList())
                .directors(Collections.emptyList())
                .mpa(new MPA(1L, "G"))
                .releaseDate(LocalDate.of(2020, 8, 25))
                .duration(100)
                .likes(Collections.emptySet())
                .build();

        Film film2 = Film.builder()
                .id(2L)
                .name("Film2")
                .description("Description2")
                .genres(Collections.emptyList())
                .directors(Collections.emptyList())
                .mpa(new MPA(1L, "G"))
                .releaseDate(LocalDate.of(2020, 8, 25))
                .duration(100)
                .likes(Collections.emptySet())
                .build();

        Film film3 = Film.builder()
                .id(3L)
                .name("Film3")
                .description("Description3")
                .genres(Collections.emptyList())
                .directors(Collections.emptyList())
                .mpa(new MPA(1L, "G"))
                .releaseDate(LocalDate.of(2020, 8, 25))
                .duration(100)
                .likes(Collections.emptySet())
                .build();

        filmStorage.add(film1);
        filmStorage.add(film2);
        filmStorage.add(film3);

        List<Film> films = filmStorage.getAll();

        assertThat(films).isNotNull();
        assertThat(films.size()).isEqualTo(3);
        assertThat(films.get(0)).isEqualTo(film1);
        assertThat(films.get(1)).isEqualTo(film2);
        assertThat(films.get(2)).isEqualTo(film3);
    }

    @Test
    public void testAddLike() {
        Film film1 = Film.builder()
                .id(1L)
                .name("Film1")
                .description("Description1")
                .genres(Collections.emptyList())
                .directors(Collections.emptyList())
                .mpa(new MPA(1L, "G"))
                .releaseDate(LocalDate.of(2020, 8, 25))
                .duration(100)
                .likes(Collections.emptySet())
                .build();

        User user1 = User.builder()
                .id(1L)
                .name("Ivan Petrov")
                .email("user1@email.ru")
                .login("vanya1")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(Collections.emptySet())
                .build();

        filmStorage.add(film1);
        userStorage.add(user1);

        assertThat(filmStorage.get(1).getLikes().size()).isEqualTo(0);

        filmStorage.addLike(1, 1);

        Film savedFilm = filmStorage.get(1);

        assertThat(savedFilm.getLikes().size()).isEqualTo(1);
        assertThat(savedFilm.getLikes().contains(1L)).isEqualTo(true);
    }

    @Test
    public void testRemoveLike() {
        Film film1 = Film.builder()
                .id(1L)
                .name("Film1")
                .description("Description1")
                .genres(Collections.emptyList())
                .directors(Collections.emptyList())
                .mpa(new MPA(1L, "G"))
                .releaseDate(LocalDate.of(2020, 8, 25))
                .duration(100)
                .likes(Collections.emptySet())
                .build();

        User user1 = User.builder()
                .id(1L)
                .name("Ivan Petrov")
                .email("user1@email.ru")
                .login("vanya1")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(Collections.emptySet())
                .build();

        filmStorage.add(film1);
        userStorage.add(user1);
        filmStorage.addLike(1, 1);

        assertThat(filmStorage.get(1).getLikes().size()).isEqualTo(1);

        filmStorage.removeLike(1, 1);

        assertThat(filmStorage.get(1).getLikes().size()).isEqualTo(0);
    }

    @Test
    public void testGetLikes() {
        Film film1 = Film.builder()
                .id(1L)
                .name("Film1")
                .description("Description1")
                .genres(Collections.emptyList())
                .directors(Collections.emptyList())
                .mpa(new MPA(1L, "G"))
                .releaseDate(LocalDate.of(2020, 8, 25))
                .duration(100)
                .likes(Collections.emptySet())
                .build();

        User user1 = User.builder()
                .id(1L)
                .name("Ivan Petrov")
                .email("user1@email.ru")
                .login("vanya1")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(Collections.emptySet())
                .build();

        User user2 = User.builder()
                .id(2L)
                .name("Ivan Petrov")
                .email("user2@email.ru")
                .login("vanya2")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(Collections.emptySet())
                .build();

        User user3 = User.builder()
                .id(3L)
                .name("Ivan Petrov")
                .email("user3@email.ru")
                .login("vanya3")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(Collections.emptySet())
                .build();

        filmStorage.add(film1);
        userStorage.add(user1);
        userStorage.add(user2);
        userStorage.add(user3);

        assertThat(filmStorage.getLikes(1).size()).isEqualTo(0);

        filmStorage.addLike(1, 1);
        filmStorage.addLike(1, 2);
        filmStorage.addLike(1, 3);

        Set<Long> likes = filmStorage.getLikes(1);

        assertThat(likes.size()).isEqualTo(3);
        assertThat(likes.contains(1L)).isEqualTo(true);
        assertThat(likes.contains(2L)).isEqualTo(true);
        assertThat(likes.contains(3L)).isEqualTo(true);
    }

    @Test
    public void testGetPopular() {
        Film film1 = Film.builder()
                .id(1L)
                .name("Film1")
                .description("Description1")
                .genres(Collections.emptyList())
                .directors(Collections.emptyList())
                .mpa(new MPA(1L, "G"))
                .releaseDate(LocalDate.of(2020, 8, 25))
                .duration(100)
                .likes(Collections.emptySet())
                .build();

        Film film2 = Film.builder()
                .id(2L)
                .name("Film2")
                .description("Description2")
                .genres(Collections.emptyList())
                .directors(Collections.emptyList())
                .mpa(new MPA(1L, "G"))
                .releaseDate(LocalDate.of(2020, 8, 25))
                .duration(100)
                .likes(Collections.emptySet())
                .build();

        Film film3 = Film.builder()
                .id(3L)
                .name("Film3")
                .description("Description3")
                .genres(Collections.emptyList())
                .directors(Collections.emptyList())
                .mpa(new MPA(1L, "G"))
                .releaseDate(LocalDate.of(2020, 8, 25))
                .duration(100)
                .likes(Collections.emptySet())
                .build();

        Film film4 = Film.builder()
                .id(4L)
                .name("Film4")
                .description("Description4")
                .genres(Collections.emptyList())
                .directors(Collections.emptyList())
                .mpa(new MPA(1L, "G"))
                .releaseDate(LocalDate.of(2020, 8, 25))
                .duration(100)
                .likes(Collections.emptySet())
                .build();

        User user1 = User.builder()
                .id(1L)
                .name("Ivan Petrov")
                .email("user1@email.ru")
                .login("vanya1")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(Collections.emptySet())
                .build();

        User user2 = User.builder()
                .id(2L)
                .name("Ivan Petrov")
                .email("user2@email.ru")
                .login("vanya2")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(Collections.emptySet())
                .build();

        User user3 = User.builder()
                .id(3L)
                .name("Ivan Petrov")
                .email("user3@email.ru")
                .login("vanya3")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(Collections.emptySet())
                .build();

        filmStorage.add(film1);
        filmStorage.add(film2);
        filmStorage.add(film3);
        filmStorage.add(film4);
        userStorage.add(user1);
        userStorage.add(user2);
        userStorage.add(user3);

        filmStorage.addLike(3, 1);
        filmStorage.addLike(3, 2);
        filmStorage.addLike(3, 3);

        filmStorage.addLike(1, 2);
        filmStorage.addLike(1, 3);

        filmStorage.addLike(2, 2);

        List<Film> popularFilms = filmStorage.getPopular(10);

        assertThat(popularFilms.size()).isEqualTo(4);
        assertThat(popularFilms.get(0).getId()).isEqualTo(3L);
        assertThat(popularFilms.get(1).getId()).isEqualTo(1L);
        assertThat(popularFilms.get(2).getId()).isEqualTo(2L);
        assertThat(popularFilms.get(3).getId()).isEqualTo(4L);

        popularFilms = filmStorage.getPopular(2);

        assertThat(popularFilms.size()).isEqualTo(2);
        assertThat(popularFilms.get(0).getId()).isEqualTo(3L);
        assertThat(popularFilms.get(1).getId()).isEqualTo(1L);
    }

    @Test
    public void testGetDirectorsWithName() {
        Film film1 = Film.builder()
                .id(1L)
                .name("Film1")
                .description("Description1")
                .genres(Collections.emptyList())
                .directors(Collections.emptyList())
                .mpa(new MPA(1L, "G"))
                .releaseDate(LocalDate.of(2020, 8, 25))
                .duration(100)
                .likes(Collections.emptySet())
                .build();

        Film film2 = Film.builder()
                .id(2L)
                .name("Film2")
                .description("Description2")
                .genres(Collections.emptyList())
                .directors(Collections.emptyList())
                .mpa(new MPA(1L, "G"))
                .releaseDate(LocalDate.of(2020, 8, 25))
                .duration(100)
                .likes(Collections.emptySet())
                .build();
        filmStorage.add(film1);
        filmStorage.add(film2);
        List<Film> expList = new ArrayList<>();
        expList.add(film1);
        List<Film> directorsList = filmStorage.getFilmWithName("1");
        assertThat(directorsList)
                .isNotEmpty()
                .isNotNull()
                .isEqualTo(expList);
    }

    @Test
    public void testNotContainFilm() {
        assertThat(filmStorage.notContainFilm(100)).isEqualTo(true);

        Film film1 = Film.builder()
                .id(1L)
                .name("Film1")
                .description("Description1")
                .genres(Collections.emptyList())
                .directors(Collections.emptyList())
                .mpa(new MPA(1L, "G"))
                .releaseDate(LocalDate.of(2020, 8, 25))
                .duration(100)
                .likes(Collections.emptySet())
                .build();

        filmStorage.add(film1);

        assertThat(filmStorage.notContainFilm(1)).isEqualTo(false);
    }
}