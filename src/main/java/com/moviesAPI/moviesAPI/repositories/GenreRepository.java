package com.moviesAPI.moviesAPI.repositories;

import com.moviesAPI.moviesAPI.Genre;
import org.springframework.data.repository.CrudRepository;

// This will be AUTO IMPLEMENTED by Spring into a Bean called characterRepository
// CRUD refers Create, Read, Update, Delete

public interface GenreRepository extends CrudRepository<Genre, Integer> {

}
