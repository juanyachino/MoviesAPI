package com.moviesAPI.moviesAPI.repositories;

import com.moviesAPI.moviesAPI.entities.Movie;
import org.springframework.data.repository.CrudRepository;

// This will be AUTO IMPLEMENTED by Spring into a Bean called characterRepository
// CRUD refers Create, Read, Update, Delete

public interface MovieRepository extends CrudRepository<Movie, Integer> {

}
