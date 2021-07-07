package com.moviesAPI.moviesAPI.controllers;




import com.moviesAPI.moviesAPI.entities.Character;
import com.moviesAPI.moviesAPI.entities.Movie;
import com.moviesAPI.moviesAPI.repositories.CharacterRepository;
import com.moviesAPI.moviesAPI.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@Controller // This means that this class is a Controller
@Validated
@RequestMapping(path="/characters") // This means URL's start with /characters (after Application path)
public class CharacterController {
    @Autowired // This means to get the bean called characterRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private CharacterRepository characterRepository;
    @Autowired
    private MovieRepository movieRepository;

    @PostMapping(path="/add") // Map ONLY POST Requests
    public @ResponseBody String addNewCharacter (@RequestParam String name,
                                                 @RequestParam String story,
                                                 @RequestParam Integer age,
                                                 @RequestParam Integer weight,
                                                 @RequestParam List<String> moviesTitles /*,
                                                 @RequestParam MultipartFile multipartImage*/) throws IOException {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request

        Character character = new Character();
        character.setName(name);
        character.setAge(age);
        character.setStory(story);
        character.setWeight(weight);
        //only previously added movies will be added to character
        for(String movieTitle : moviesTitles){
            List<Movie> movies = movieRepository.findByTitle(movieTitle);
            if (!movies.isEmpty()) {
                Movie movie = movies.get(0);
                character.getMovies().add(movie);
                movie.getCharacters().add(character);
            }
        }
       // character.setImage(multipartImage.getBytes());
        characterRepository.save(character);
        return "Saved";
    }
    @PostMapping(path="/edit")
    @Description("Edits any character's field. editing movies removes the previously saved movies!")
    public @ResponseBody String editCharacter (@RequestParam String name,
                                                 @RequestParam(required = false) String story,
                                                 @RequestParam(required = false) Integer age,
                                                 @RequestParam(required = false) Integer weight,
                                                 @RequestParam(required = false) List<String> moviesTitles /*,
                                                 @RequestParam(required = false) MultipartFile multipartImage*/) throws IOException {
        List<Character> characters = characterRepository.findByName(name);
        if (characters.isEmpty()) {
            return "Character doesn't exist!";
        }
        Character character = characters.get(0);
        if (story != null) {
            character.setStory(story);
        }
        if (age != null) {
            character.setAge(age);
        }
        if (weight != null) {
            character.setWeight(weight);
        }
        if (moviesTitles != null) {
            character.setMovies(new HashSet<>());  //removes previously added movies!
            for(String movieTitle : moviesTitles){
                List<Movie> movies = movieRepository.findByTitle(movieTitle);
                if (!movies.isEmpty()) {
                    Movie movie = movies.get(0);
                    character.getMovies().add(movie);
                    movie.getCharacters().add(character);
                }
            }
        }
        characterRepository.save(character);
        return "Updated!";
    }
    @DeleteMapping(path="/delete")
    public @ResponseBody String deleteCharacter(@RequestParam String name) {
        List<Character> characters = characterRepository.findByName(name);
        if (characters.isEmpty()) {
            return "character not found";
        }
        characterRepository.delete(characters.get(0));
        return "deleted!";
    }
    @GetMapping(path= "/detail")
    public @ResponseBody Character getCharacterDetail(@RequestParam String name) {
        List<Character> characters = characterRepository.findByName(name);
        if (characters.isEmpty()) {
            return null;
        }
        return characters.get(0);
    }
    /*@GetMapping(path="/")
    public @ResponseBody Iterable<Character> getAllCharacters() {
        // This returns a JSON or XML with the characters
        return characterRepository.findAll();
    } */
    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody
    Object filterBy(@RequestParam(value = "age",required = false) Integer age,
                    @RequestParam(value = "name",required = false) String name,
                    @RequestParam(value = "weight",required = false) Integer weight,
                    @RequestParam(value = "movieId",required = false) Long movieId) {
        if (age != null) {
            return characterRepository.findByAge(age);
        }
        if (name != null) {
            return characterRepository.findByName(name);
        }
        if (weight != null) {
            return characterRepository.findByWeight(weight);
        }
        if (movieId != null) {
            Optional<Movie> movies = movieRepository.findById(movieId);
            if (movies.isPresent()){
                return movies.get().getCharacters();
            }
        }
        return characterRepository.findBy(); //returns all characters in list view if no params were given
    }
}