package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.MPAService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class MPAController {
    private final MPAService mpaService;

    @Autowired
    public MPAController(MPAService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping("/{id}")
    public MPA getFilm(@PathVariable long id) {
        log.info("Получен GET-запрос к /mpa/{}.", id);
        return mpaService.getRating(id);
    }

    @GetMapping
    public List<MPA> getFilms() {
        log.info("Получен GET-запрос к /mpa.");
        return mpaService.getAllRatings();
    }
}