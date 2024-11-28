package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.MpaStorage;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;

@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    @Autowired
    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public Rating create(Rating rating) {
        return mpaStorage.create(rating);
    }

    public Rating update(Rating rating) {
        return mpaStorage.update(rating);
    }

    public Integer delete(Integer mpaId) {
        return mpaStorage.delete(mpaId);
    }

    public Collection<Rating> fingAll() {
        return mpaStorage.findAll();
    }

    public Rating findOne(Integer mpaId) {
        return mpaStorage.getMpaById(mpaId);
    }
}
