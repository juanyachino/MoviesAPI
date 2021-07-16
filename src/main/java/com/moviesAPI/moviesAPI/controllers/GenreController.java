package com.moviesAPI.moviesAPI.controllers;


import com.moviesAPI.moviesAPI.services.GenreServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
}
