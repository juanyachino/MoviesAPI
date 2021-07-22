package com.moviesAPI.repositories;

import com.moviesAPI.entities.Movie;
import org.springframework.data.repository.CrudRepository;

// This will be AUTO IMPLEMENTED by Spring into a Bean called characterRepository
// CRUD refers Create, Read, Update, Delete

public interface MovieRepository extends CrudRepository<Movie, Long> {
    Iterable<Movie> findByTitle(String title);

    Iterable<Movie> findByOrderByReleaseYearAsc();
    Iterable<Movie> findByOrderByReleaseYearDesc();
    Iterable<MoviesListView> findBy();


    interface MoviesListView {

        byte[] getImage();
        String getTitle();
        String getReleaseYear();
    }
}
