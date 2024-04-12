package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.feed.FeedEntity;
import ru.yandex.practicum.filmorate.model.feed.FeedEventType;
import ru.yandex.practicum.filmorate.model.feed.FeedOperation;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final FeedStorage feedStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("feedDbStorage") FeedStorage feedStorage,
                       @Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.feedStorage = feedStorage;
        this.filmStorage = filmStorage;
    }

    public User addUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        User savedUser = userStorage.add(user);
        log.info("Пользователь добавлен. User: {}", savedUser);
        return savedUser;
    }

    public User updateUser(User user) {
        User savedUser = userStorage.update(user);
        log.info("Данные пользователя обновлены. User: {}", savedUser);
        return savedUser;
    }

    public User getUser(long id) {
        User user = userStorage.get(id);
        log.info("Получен пользователь с id {}. User: {}", id, user);
        return user;
    }

    public void deleteUser(long id) {
        userStorage.delete(id);
        log.info("Пользователь удален id: {}", id);
    }

    public List<User> getAllUsers() {
        List<User> users = userStorage.getAll();
        log.info("Получен список всех пользователей. List<User>: {}", users);
        return users;
    }

    public User addFriend(long id, long followerId) {
        User user = userStorage.addFriend(id, followerId);
        log.info("Сохранена заявка на добавление в друзья пользователю с id {} от пользователя с id {}. " +
                        "User (id: {}): {}",
                id, followerId, id, user);

        FeedEventType eventType = FeedEventType.FRIEND;
        FeedOperation operation = FeedOperation.ADD;

        feedStorage.add(id, followerId, eventType, operation);
        log.debug(
                "Заявка на добавление пользователя с id {} в друзья пользователем с id {} добавлена в таблицу feed. " +
                        "userId = {}, entityId = {}, eventType = {}, operation = {}",
                id, followerId, followerId, id, eventType, operation
        );
        return user;
    }

    public User removeFriend(long id, long followerId) {
        User user = userStorage.removeFriend(id, followerId);
        log.info("Удалена заявка на добавление в друзья пользователю с id {} от пользователя с id {}. " +
                        "User (id: {}): {}",
                id, followerId, id, user);

        FeedEventType eventType = FeedEventType.FRIEND;
        FeedOperation operation = FeedOperation.REMOVE;

        feedStorage.add(id, followerId, eventType, operation);
        log.debug(
                "Удаление пользователя с id {} из друзей пользователя с id {} добавлено в таблицу feed. " +
                        "userId = {}, entityId = {}, eventType = {}, operation = {}",
                id, followerId, followerId, id, eventType, operation
        );
        return user;
    }

    public List<User> getFriends(long id) {
        List<User> friends = userStorage.getFriends(id);
        log.info("Получен список всех друзей пользователя с id {}. List<User>: {}", id, friends);
        return friends;
    }

    public List<User> getCommonFriends(long id, long otherId) {
        List<User> friends = userStorage.getCommonFriends(id, otherId);
        log.info("Получен список общих друзей пользователей с id {} и {}. List<User>: {}", id, otherId, friends);
        return friends;
    }

    public List<FeedEntity> getUserFeed(long userId) {
        List<FeedEntity> feed = userStorage.getFeed(userId);
        log.info("Получен список последних событий на платформе для пользователя с id {}. List<FeedEntity>: {}",
                userId, feed);
        return userStorage.getFeed(userId);
    }

    public Set<Film> getRecommendations(Long id) {

        if (userStorage.notContainUser(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("User id", String.format("пользователь с id: %d не найден.", id))
            );
        }
        Set<Film> result = new HashSet<>();
        Set<Long> allUsersId = getAllUsers().stream().map(User::getId).collect(Collectors.toSet());

        log.info("Поиск пользователя с наибольшим пересечением");
        long maxInters = 0;
        long userIdWithMaxInters = -1;
        Set<Long> filmsIdRecommended = null;

        for (Long otherUserId : allUsersId) {
            if (!id.equals(otherUserId)) {
                Set<Long> likesListByOtherUser = userStorage.findFilmsWithLikes(otherUserId);
                Set<Long> intersectionList = new HashSet<>(userStorage.findFilmsWithLikes(id));
                intersectionList.retainAll(likesListByOtherUser);
                if (intersectionList.size() > maxInters) {
                    maxInters = intersectionList.size();
                    userIdWithMaxInters = otherUserId;
                    intersectionList.forEach(likesListByOtherUser::remove);
                    filmsIdRecommended = likesListByOtherUser;
                }
            }
        }

        log.info("Фильмы пользователя с наибольшим пересечением");
        if (userIdWithMaxInters != -1 || filmsIdRecommended != null) {
            filmsIdRecommended.forEach(filmId -> result.add(filmStorage.get(filmId)));
        }
        return result;
    }

}
