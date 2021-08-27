package com.moviesAPI.controllers;




import com.moviesAPI.DTO.CharacterDTO;
import com.moviesAPI.DTO.EditCharacterDTO;
import com.moviesAPI.entities.Character;


import com.moviesAPI.services.CharacterServices;
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
@RequestMapping(path="/characters") // This means URL's start with /characters (after Application path)
public class CharacterController {
    @Autowired
    private CharacterServices characterServices;

    @PostMapping("/add")
    public ResponseEntity<String> addNewCharacter(@RequestPart("data")@Valid CharacterDTO characterDTO,
                                                  @RequestPart("file")@Valid @NotNull MultipartFile file)  {
        characterServices.createCharacter(characterDTO,file);
        return new ResponseEntity<>(
                "Character created successfully!",
                HttpStatus.OK);
    }
    @PostMapping(path="/edit")
    @Description("Edits any character's field. editing movies removes the previously saved movies!")
    public @ResponseBody ResponseEntity<String> editCharacter (@RequestPart("data")@Valid EditCharacterDTO characterDTO,
                                                               @RequestPart("file")@Valid MultipartFile file) {

        return characterServices.editCharacter(characterDTO,file) ?
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
    Iterable filterBy(@RequestParam(value = "age",required = false) Integer age,
                    @RequestParam(value = "name",required = false) String name,
                    @RequestParam(value = "weight",required = false) Integer weight,
                    @RequestParam(value = "movieId",required = false) Long movieId) {

        return characterServices.getFilteredCharacterList(age, weight, name, movieId);
    }
}