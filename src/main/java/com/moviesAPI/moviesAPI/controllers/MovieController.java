package com.moviesAPI.moviesAPI.controllers;


import com.moviesAPI.moviesAPI.entities.Movie;
import com.moviesAPI.moviesAPI.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Controller // This means that this class is a Controller
@RequestMapping(path="/movies") // This means URL's start with /movies (after Application path)
public class MovieController {
    @Autowired // This means to get the bean called movieRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private MovieRepository movieRepository;

    @PostMapping(path="/add") // Map ONLY POST Requests
    public @ResponseBody String addNewMovie (@RequestParam String title,
                                                 @RequestParam String date,
                                                 @RequestParam Integer rating,
                                                 @RequestParam MultipartFile multipartImage) throws IOException {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request

        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setRating(rating);
        movie.setDate(date);
        movie.setImage(multipartImage.getBytes());
        movieRepository.save(movie);
        return "Saved";
    }

    @GetMapping(path="/")
    public @ResponseBody Iterable<Movie> getAllMovies() {
        // This returns a JSON or XML with the characters
        return movieRepository.findAll();
    }
}