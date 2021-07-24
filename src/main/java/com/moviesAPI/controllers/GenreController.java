package com.moviesAPI.controllers;


import com.moviesAPI.entities.Character;
import com.moviesAPI.entities.Genre;
import com.moviesAPI.services.GenreServices;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(path="/genres") // This means URL's start with /genres (after Application path)
public class GenreController {
    @Autowired
    private GenreServices genreServices;

    @PostMapping(path="/add") // Map ONLY POST Requests
    public @ResponseBody
    String addNewGenre (@RequestParam String name, @RequestParam(required = false) List<Long> moviesIds ,
                                                 @RequestParam MultipartFile multipartImage) throws IOException {


        genreServices.createGenre(name,moviesIds,multipartImage);
        return name +" Saved";
    }
    @DeleteMapping(path="/delete")
    public @ResponseBody String deleteGenre(@RequestParam Long id) {
        return genreServices.deleteGenre(id) ? "deleted!" : "Genre not found!";
    }
    @GetMapping(path= "/detail")
    public @ResponseBody
    ResponseEntity getGenreDetails(@RequestParam Long id) {
        Genre genre = genreServices.getGenreDetails(id);
        return genre != null ?
                new ResponseEntity<>(
                        genre,
                        HttpStatus.OK) :
                new ResponseEntity<>(
                        "Genre doesn't exist",
                        HttpStatus.BAD_REQUEST);
    }

}
