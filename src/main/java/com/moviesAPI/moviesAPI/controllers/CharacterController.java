package com.moviesAPI.moviesAPI.controllers;




import com.moviesAPI.moviesAPI.entities.Character;
import com.moviesAPI.moviesAPI.services.CharacterServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Controller // This means that this class is a Controller
@Validated
@RequestMapping(path="/characters") // This means URL's start with /characters (after Application path)
public class CharacterController {
    @Autowired
    private CharacterServices characterServices;

    @PostMapping(path="/add") // Map ONLY POST Requests
    public @ResponseBody String addNewCharacter (@RequestParam String name,
                                                 @RequestParam String story,
                                                 @RequestParam Integer age,
                                                 @RequestParam Integer weight,
                                                 @RequestParam(required = false) List<Long> moviesIds ,
                                                 @RequestParam MultipartFile multipartImage) throws IOException {

        characterServices.createCharacter(name,story,age,weight,moviesIds,multipartImage);
        return "Saved";
    }
    @PostMapping(path="/edit")
    @Description("Edits any character's field. editing movies removes the previously saved movies!")
    public @ResponseBody ResponseEntity<String> editCharacter (@RequestParam Long id,
                                               @RequestParam(required = false) String name,
                                                 @RequestParam(required = false) String story,
                                                 @RequestParam(required = false) Integer age,
                                                 @RequestParam(required = false) Integer weight,
                                                 @RequestParam(required = false) List<Long> moviesIds ,
                                                 @RequestParam(required = false) MultipartFile multipartImage) throws IOException {

        return characterServices.editCharacter(id, name, story, age, weight, moviesIds, multipartImage) ?
                new ResponseEntity<>(
                        "Character updated successfully",
                        HttpStatus.OK) :
                new ResponseEntity<>(
                        "Character doesn't exist",
                        HttpStatus.BAD_REQUEST);
    }
    @DeleteMapping(path="/delete")
    public @ResponseBody String deleteCharacter(@RequestParam Long id) {
        return characterServices.deleteCharacter(id) ? "deleted!" : "character not found!";
    }
    @GetMapping(path= "/detail")
    public @ResponseBody
    ResponseEntity getCharacterDetail(@RequestParam Long id) {
        Character character = characterServices.getCharacterDetails(id);
        return character != null ?
                new ResponseEntity<>(
                        character,
                        HttpStatus.OK) :
                new ResponseEntity<>(
                        "Character doesn't exist",
                        HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody
    Object filterBy(@RequestParam(value = "age",required = false) Integer age,
                    @RequestParam(value = "name",required = false) String name,
                    @RequestParam(value = "weight",required = false) Integer weight,
                    @RequestParam(value = "movieId",required = false) Long movieId) {

        return characterServices.getFilteredCharacterList(age, weight, name, movieId);
    }
}