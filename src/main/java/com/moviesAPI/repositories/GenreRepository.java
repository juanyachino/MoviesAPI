package com.moviesAPI.repositories;

import com.moviesAPI.entities.Genre;
import org.springframework.data.repository.CrudRepository;

// This will be AUTO IMPLEMENTED by Spring into a Bean called characterRepository
// CRUD refers Create, Read, Update, Delete

public interface GenreRepository extends CrudRepository<Genre, Long> {
    Iterable<Genre> findByName(String name);

}
