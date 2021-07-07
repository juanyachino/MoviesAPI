package com.moviesAPI.moviesAPI;


import com.moviesAPI.moviesAPI.entities.Character;
import com.moviesAPI.moviesAPI.entities.Movie;
import com.moviesAPI.moviesAPI.repositories.CharacterRepository;
import com.moviesAPI.moviesAPI.repositories.GenreRepository;
import com.moviesAPI.moviesAPI.repositories.MovieRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Arrays;

@SpringBootApplication
@EnableWebSecurity
@RestController
public class MoviesApiApplication {

	public static void main(String[] args) {
		org.springframework.context.ConfigurableApplicationContext run = SpringApplication.run(MoviesApiApplication.class, args);
	}

	@GetMapping("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}

	@Bean
	public CommandLineRunner mappingDemo(CharacterRepository characterRepository,
										 GenreRepository genreRepository,
										 MovieRepository movieRepository) {
		return args -> {

			// create a character
			Character character = new Character();
			character.setName("Captain Marvel");
			character.setWeight(70);
			character.setAge(36);
			character.setStory("A human-kree female that used to be an aviator on Earth.");

			// save the character
			characterRepository.save(character);

			// create a character
			Character character2 = new Character();
			character2.setName("Thanos");
			character2.setWeight(302);
			character2.setAge(99999999);
			character2.setStory("the mad titan.");

			// save the character
			characterRepository.save(character2);
			// create three movies
			Movie movie1 = new Movie();
			movie1.setTitle("Captain Marvel");
			movie1.setDate("2019");
			movie1.setRating(4);

			Movie movie2 = new Movie();
			movie2.setTitle("Avengers: endgame");
			movie2.setDate("2019");
			movie2.setRating(5);

			Movie movie3 = new Movie();
			movie3.setTitle("Captain Marvel 2");
			movie3.setDate("TBA");

			// save movies
			movieRepository.saveAll(Arrays.asList(movie1,movie3,movie2));

			// add movies to the character
			character.getMovies().addAll(Arrays.asList(movie1, movie3, movie2));

			// update the character
			characterRepository.save(character);
		};
	}
}

