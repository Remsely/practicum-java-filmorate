package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Slf4j
@Service
public class GenreService {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Genre getGenre(long id) {
        Genre genre = genreStorage.get(id);
        log.info("Получен жанр с id {}. Genre: {}", id, genre);
        return genre;
    }

    public List<Genre> getAllGenres() {
        List<Genre> genres = genreStorage.getAll();
        log.info("Получен список всех жанров. List<Genre>: {}", genres);
        return genres;
    }
}