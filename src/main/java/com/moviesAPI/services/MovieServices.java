package com.moviesAPI.services;

import com.moviesAPI.entities.Character;
import com.moviesAPI.entities.Genre;
import com.moviesAPI.entities.Movie;
import com.moviesAPI.exceptions.InvalidReleaseYearException;
import com.moviesAPI.exceptions.InvalidMovieRatingException;
import com.moviesAPI.repositories.CharacterRepository;
import com.moviesAPI.repositories.GenreRepository;
import com.moviesAPI.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class MovieServices {
    @Autowired // This means to get the bean called characterRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private CharacterRepository characterRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private GenreRepository genreRepository;

    public void createMovie (String title, Integer releaseYear, Integer rating, List<Long> genresIds,
                             List<Long> charactersIds, MultipartFile multipartImage)
            throws IOException, InvalidReleaseYearException, InvalidMovieRatingException {
        if (releaseYear <= 1900) {
            throw new InvalidReleaseYearException("Movie release year can't be prior to 1900");
        }
        if (rating < 1 || rating > 5) {
            throw new InvalidMovieRatingException("Movie rating has to be a value between 1 and 5");
        }
        Movie movie = new Movie(multipartImage.getBytes(), title,releaseYear,rating);
        if (charactersIds != null) {
            addCharacters(movie,charactersIds);
        }
        //only previously added genres will be added to movie
        if (genresIds != null) {
            addGenres(movie,genresIds);
        }
        movieRepository.save(movie);
    }
    public boolean editMovie (Long id,String title, Integer releaseYear, Integer rating,List<Long> charactersIds,
                              List<Long> genresIds,
                              MultipartFile multipartImage) throws IOException, InvalidReleaseYearException, InvalidMovieRatingException {
        Optional<Movie> movies = movieRepository.findById(id);


        if (!movies.isPresent()) {
            return false;
        }
        Movie movie = movies.get();
        if (title != null) {
            movie.setTitle(title);
        }
        if (releaseYear != null) {
            if (releaseYear <= 1900) {
                throw new InvalidReleaseYearException("Movie release year can't be prior to 1900");
            }
            movie.setReleaseYear(releaseYear);
        }
        if (rating != null) {
            if (rating < 1 || rating > 5) {
                throw new InvalidMovieRatingException("Movie rating has to be a value between 1 and 5");
            }
            movie.setRating(rating);
        }

        if (charactersIds != null) {
            movie.setCharacters(new HashSet<>());  //removes previously added characters!
            addCharacters(movie,charactersIds);
        }
        if (genresIds != null) {
            movie.setCharacters(new HashSet<>());  //removes previously added genres!
            addGenres(movie,genresIds);
        }
        if (multipartImage != null) {
            movie.setImage(multipartImage.getBytes());
        }
        movieRepository.save(movie);
        return true;
    }
    public Iterable getFilteredMoviesList(Long genreId, String title,String orderBy){
        if (title != null) {
            return movieRepository.findByTitle(title);
        }
        if (orderBy != null) {
            return "ASC".equals(orderBy) ? movieRepository.findByOrderByReleaseYearAsc() :
                    movieRepository.findByOrderByReleaseYearDesc();
        }
        if (genreId != null) {
            Optional<Genre> genresFound = genreRepository.findById(genreId);
            return genresFound.<Iterable>map(Genre::getMovies).orElse(null);
        }
        return movieRepository.findBy(); //returns all movies in list view if no params were given
    }
    public boolean deleteMovie (Long id) {
        Optional<Movie> movieFound = movieRepository.findById(id);
        if (!movieFound.isPresent()) {
            return false;
        }
        movieRepository.delete(movieFound.get());
        return true;
    }
    public Movie getMovieDetails(Long id) {
        return movieRepository.findById(id).orElse(null);
    }
    private Movie addGenres( Movie movie , List<Long> genresIds){
        for (Long genreId : genresIds) {
            Optional<Genre> genres = genreRepository.findById(genreId);
            if (genres.isPresent()) {
                Genre genre = genres.get();
                genre.getMovies().add(movie);
            }
        }
        return movie;
    }
    private Movie addCharacters( Movie movie , List<Long> charactersIds){
        for (Long characterId : charactersIds) {
            Optional<Character> characters = characterRepository.findById(characterId);
            if (characters.isPresent()) {
                Character character = characters.get();
                character.getMovies().add(movie);
            }
        }
        return movie;
    }
}