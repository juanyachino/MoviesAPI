package com.moviesAPI.moviesAPI.controllers;



import com.moviesAPI.moviesAPI.entities.Movie;
import com.moviesAPI.moviesAPI.repositories.CharacterRepository;
import com.moviesAPI.moviesAPI.repositories.GenreRepository;
import com.moviesAPI.moviesAPI.repositories.MovieRepository;
import com.moviesAPI.moviesAPI.services.MovieServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import java.util.List;


@Controller // This means that this class is a Controller
@Validated
@RequestMapping(path="/movies") // This means URL's start with /movies (after Application path)
public class MovieController {

    @Autowired
    private MovieServices movieServices;

    @PostMapping(path="/add") // Map ONLY POST Requests
    public @ResponseBody String addNewMovie (@RequestParam String title,
                                                 @RequestParam String date,
                                                 @RequestParam Integer rating,
                                                 @RequestParam(required = false) List<Long> genresIds,
                                                 @RequestParam(required = false) List<Long> charactersIds,
                                                 @RequestParam MultipartFile multipartImage) throws IOException {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request
        movieServices.createMovie(title, date, rating, genresIds, charactersIds, multipartImage);
        return title +" Saved";
    }

    @PostMapping(path="/edit")
    @Description("Edits any movie's field. editing characters/genres removes the previously saved characters/genres!")
    public @ResponseBody ResponseEntity<String> editMovie (@RequestParam Long id, @RequestParam(required = false) String title,
                                               @RequestParam(required = false) String date,
                                               @RequestParam(required = false) Integer rating,
                                               @RequestParam(required = false) List<Long> charactersIds,
                                               @RequestParam(required = false) List<Long> genresIds,
                                               @RequestParam(required = false) MultipartFile multipartImage) throws IOException {
        return movieServices.editMovie(id, title, date, rating, charactersIds, genresIds, multipartImage)?
                new ResponseEntity<>(
                        "Movie with Id: "+ id +" updated successfully",
                        HttpStatus.OK) :
                new ResponseEntity<>(
                        "Movie with Id: "+ id +"doesn't exist",
                        HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(path="/delete")
    public @ResponseBody String deleteMovie(@RequestParam Long id) {
        return movieServices.deleteMovie(id) ? "deleted!" : "Movie not found!";
    }

    @GetMapping(path= "/detail")
    public @ResponseBody ResponseEntity getMovieDetail(@RequestParam Long id) {
        Movie movie = movieServices.getMovieDetails(id);
        return movie != null ?
                new ResponseEntity<>(
                        movie,
                        HttpStatus.OK) :
                new ResponseEntity<>(
                        "Movie with Id: "+ id +" doesn't exist",
                        HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody
    Object filterBy(@RequestParam(value = "title",required = false) String title,
                    @RequestParam(value = "genreId",required = false) Long genreId,
                    @RequestParam(value = "orderBy",required = false) String orderBy) {

        return movieServices.getFilteredMoviesList(genreId, title, orderBy);
    }
}