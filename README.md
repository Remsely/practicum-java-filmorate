# Filmorate

Spring Boot приложение, которое работает с фильмами и оценками пользователей, а также возвращает топ фильмов,
рекомендованных к просмотру.

Приложение написано на языке Java с использованием системы сборки Maven. Также в приложении используются технологии
Lombok, Spring Validation, PostgreSQL.

## Функционал

Приложение представляет собой Rest-API, реализующее следующие эндпоинты:

+ POST /users - Добавление пользователя;
+ PUT /users - Обновление пользователя;
+ GET /users/{id} - Получение пользователя по id;
+ PUT /users/{id}/friends/{friendId} - Добавление пользователя friendId в друзья к пользователю id;
+ DELETE /users/{id}/friends/{friendId} - Удаление пользователя friendId из друзей пользователя id;
+ GET /users/{id}/friends - Получение списка друзей пользователя id;
+ GET /users/{id}/friends/common/{otherId} - Получение списка общих друзей пользователей id и otherId;


+ POST /films - Добавление фильма;
+ PUT /films - Обновление фильма;
+ GET /films/{id} - Получение фильма по id;
+ PUT /films/{id}/like/{userId} - Добавление лайка фильму id от пользователя userId;
+ DELETE /films/{id}/like/{userId} - Удаление лайка фильма id от пользователя userId;
+ GET /films/popular?count={count} - Получение первых count фильмов по кол-ву лайков.

## Архитектура БД

Схема базы данных приложения представлена на рисунке.

![Схема БД](docs/DB_diagram.png)

### Примеры запросов к БД

**Получение пользователя по ID**

```postgresql
SELECT *
FROM user
WHERE user_id = id;
```

**Получение фильма по ID**

```postgresql
SELECT *
FROM film
WHERE film_id = id;
```

**Получение списка друзей пользователя по ID**

```postgresql
SELECT followed_user_id
FROM follow
WHERE following_user_id = id
  AND approved = true;
```

**Получение списка общих друзей пользователей id и otherId**

```postgresql
SELECT followed_user_id
FROM follow
WHERE following_user_id = otherId
  AND followed_user_id IN (SELECT followed_user_id
                           FROM follow
                           WHERE following_user_id = id
                             AND approved = true)
  AND approved = true;
```

**Получение первых counter фильмов по кол-ву лайков**

```postgresql
SELECT film_id
FROM like
GROUP BY film_id
ORDER BY COUNT(user_id) DESC
LIMIT counter;
```