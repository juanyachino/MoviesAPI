package com.moviesAPI.moviesAPI.repositories;

import com.moviesAPI.moviesAPI.entities.Movie;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

// This will be AUTO IMPLEMENTED by Spring into a Bean called characterRepository
// CRUD refers Create, Read, Update, Delete

public interface MovieRepository extends CrudRepository<Movie, Long> {
    List<Movie> findByTitle(String title);

}
