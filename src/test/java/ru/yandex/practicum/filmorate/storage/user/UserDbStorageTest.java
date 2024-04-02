package ru.yandex.practicum.filmorate.storage.user;

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
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MPADbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MPAStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private UserDbStorage userStorage;
    private FilmDbStorage filmStorage;

    @BeforeEach
    void init() {
        GenreStorage genreStorage = new GenreDbStorage(jdbcTemplate);
        MPAStorage mpaStorage = new MPADbStorage(jdbcTemplate);
        filmStorage = new FilmDbStorage(jdbcTemplate, genreStorage, mpaStorage);
        userStorage = new UserDbStorage(jdbcTemplate);
    }

    @Test
    public void testAddUser() {
        assertThatCode(() -> {
            User newUser = User.builder()
                    .id(1L)
                    .name("Ivan Petrov")
                    .email("user@email.ru")
                    .login("vanya123")
                    .birthday(LocalDate.of(1990, 1, 1))
                    .friends(Collections.emptySet())
                    .build();

            userStorage.add(newUser);
        }).doesNotThrowAnyException();
    }

    @Test
    public void testUpdateUser() {
        User newUser = User.builder()
                .id(1L)
                .name("Ivan Petrov")
                .email("user@email.ru")
                .login("vanya123")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(Collections.emptySet())
                .build();

        userStorage.add(newUser);

        User updatedUser = User.builder()
                .id(1L)
                .name("Ivan Ivanov")
                .email("user@email.ru")
                .login("vanya321")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(Collections.emptySet())
                .build();

        userStorage.update(updatedUser);

        User savedUser = userStorage.get(1);

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(updatedUser);
    }

    @Test
    public void testGetUser() {
        User newUser = User.builder()
                .id(1L)
                .name("Ivan Petrov")
                .email("user@email.ru")
                .login("vanya123")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(Collections.emptySet())
                .build();

        userStorage.add(newUser);

        User savedUser = userStorage.get(1);

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);
    }

    @Test
    public void testGetAllUsers() {
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

        userStorage.add(user1);
        userStorage.add(user2);
        userStorage.add(user3);

        List<User> users = userStorage.getAll();

        assertThat(users).isNotNull();
        assertThat(users.size()).isEqualTo(3);
        assertThat(users.get(0)).isEqualTo(user1);
        assertThat(users.get(1)).isEqualTo(user2);
        assertThat(users.get(2)).isEqualTo(user3);
    }

    @Test
    public void testAddFriend() {
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

        userStorage.add(user1);
        userStorage.add(user2);
        userStorage.addFriend(1, 2);

        user1 = userStorage.get(1);
        user2 = userStorage.get(2);

        assertThat(user1.getFriends().size()).isEqualTo(1);
        assertThat(user1.getFriends().contains(2L)).isEqualTo(true);
        assertThat(user2.getFriends().size()).isEqualTo(0);

        userStorage.addFriend(2, 1);

        user1 = userStorage.get(1);
        user2 = userStorage.get(2);

        assertThat(user1.getFriends().size()).isEqualTo(1);
        assertThat(user1.getFriends().contains(2L)).isEqualTo(true);
        assertThat(user2.getFriends().size()).isEqualTo(1);
        assertThat(user2.getFriends().contains(1L)).isEqualTo(true);
    }

    @Test
    public void testRemoveFriend() {
        User user1 = User.builder()
                .id(1L)
                .name("Ivan Petrov")
                .email("user1@email.ru")
                .login("vanya1")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(Collections.emptySet())
                .build();

        User user2 = User.builder()
                .id(1L)
                .name("Ivan Petrov")
                .email("user2@email.ru")
                .login("vanya2")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(Collections.emptySet())
                .build();

        userStorage.add(user1);
        userStorage.add(user2);
        userStorage.addFriend(2, 1);

        assertThat(userStorage.get(2).getFriends().size()).isEqualTo(1);

        userStorage.removeFriend(2, 1);

        assertThat(userStorage.get(2).getFriends().size()).isEqualTo(0);
    }

    @Test
    public void testGetCommonFriends() {
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

        userStorage.add(user1);
        userStorage.add(user2);
        userStorage.add(user3);

        userStorage.addFriend(3, 1);
        userStorage.addFriend(1, 2);
        userStorage.addFriend(3, 2);

        assertThat(userStorage.get(1).getFriends().size()).isEqualTo(1);
        assertThat(userStorage.get(2).getFriends().size()).isEqualTo(0);
        assertThat(userStorage.get(3).getFriends().size()).isEqualTo(2);

        List<User> commonFriends = userStorage.getCommonFriends(1, 3);

        assertThat(commonFriends.size()).isEqualTo(1);
        assertThat(commonFriends.get(0).getId()).isEqualTo(2);
    }

    @Test
    public void testNotContainUser() {
        assertThat(userStorage.notContainUser(100)).isEqualTo(true);

        User user1 = User.builder()
                .id(1L)
                .name("Ivan Petrov")
                .email("user1@email.ru")
                .login("vanya1")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(Collections.emptySet())
                .build();

        userStorage.add(user1);

        assertThat(userStorage.notContainUser(1)).isEqualTo(false);
    }

    @Test
    public void testGetLike(){
        Film film = Film.builder()
                .id(1L)
                .name("Film1")
                .description("Description1")
                .genres(Collections.emptyList())
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

        userStorage.add(user1);
        userStorage.add(user2);
        userStorage.addFriend(1, 2);
        filmStorage.add(film);
        filmStorage.addLike(film.getId(),user1.getId());
        filmStorage.addLike(film.getId(),user2.getId());
        List<Long> test = new ArrayList<>();
        test.add(1L);
        assertThat(userStorage.getLikes(1)).isNotEmpty().isNotNull().isEqualTo(test);
        assertThat(userStorage.getLikes(2)).isNotEmpty().isNotNull().isEqualTo(test);
    }
}