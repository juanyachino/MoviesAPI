package com.moviesAPI.controllers;



import com.moviesAPI.DTO.EditMovieDTO;
import com.moviesAPI.DTO.MovieDTO;
import com.moviesAPI.entities.Movie;
import com.moviesAPI.services.MovieServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.validation.Valid;

import javax.validation.constraints.NotNull;





@Controller // This means that this class is a Controller
@Validated
@RequestMapping(path="/movies") // This means URL's start with /movies (after Application path)
public class MovieController {

    @Autowired
    private MovieServices movieServices;

    @PostMapping(path="/add") // Map ONLY POST Requests
    public @ResponseBody ResponseEntity<String> addNewMovie (@RequestPart("data")@Valid MovieDTO movieDTO ,
                                                             @RequestPart("file")@Valid @NotNull MultipartFile file ){

        movieServices.createMovie(movieDTO,file);

        return new ResponseEntity<>(
                "Movie  "+ movieDTO.getTitle() +" created successfully",
                HttpStatus.OK);
    }
    @PostMapping(path="/edit")
    @Description("Edits any movie's field. editing characters/genres removes the previously saved characters/genres!")
    public @ResponseBody ResponseEntity<String> editMovie (@RequestPart("data")@Valid EditMovieDTO movieDTO ,
                                                           @RequestPart("file")@Valid MultipartFile file ) {

        return movieServices.editMovie(movieDTO,file)?
                new ResponseEntity<>(
                        "Movie with Id: "+ movieDTO.getId() +" updated successfully",
                        HttpStatus.OK) :
                new ResponseEntity<>(
                        "Movie with Id: "+ movieDTO.getId() +"doesn't exist",
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