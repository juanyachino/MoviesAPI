package com.moviesAPI.moviesAPI.repositories;

import com.moviesAPI.moviesAPI.entities.Genre;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

// This will be AUTO IMPLEMENTED by Spring into a Bean called characterRepository
// CRUD refers Create, Read, Update, Delete

public interface GenreRepository extends CrudRepository<Genre, Long> {
    List<Genre> findByName(String name);

}
