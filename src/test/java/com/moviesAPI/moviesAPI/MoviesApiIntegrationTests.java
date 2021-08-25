package com.moviesAPI.moviesAPI;

import com.moviesAPI.entities.Character;
import com.moviesAPI.entities.Genre;
import com.moviesAPI.entities.Movie;
import com.moviesAPI.repositories.CharacterRepository;
import com.moviesAPI.repositories.GenreRepository;
import com.moviesAPI.repositories.MovieRepository;
import com.moviesAPI.utils.MultiPartResource;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.core.io.ByteArrayResource;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;


import java.io.IOException;
import java.util.Iterator;


import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties="spring.jpa.hibernate.ddl-auto=create-drop") // clears the database after every test run
@TestPropertySource(properties="spring.datasource.url=jdbc:mysql://${MYSQL_HOST:localhost}:3306/movies_dev") // uses a different database
public class MoviesApiIntegrationTests {
    private static final String TESTUSERNAME = "test1";
    private static final String TESTPASSWORD = "test123";
    private static final String TESTEMAIL = "test@email.com";
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CharacterRepository characterRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private GenreRepository genreRepository;

    MultipartFile image = new MockMultipartFile("image",new byte[12]);

    String token = "";

    @BeforeAll
    public void setup() throws IOException {
        //User account creation
        LinkedMultiValueMap map = new LinkedMultiValueMap();
        map.add("username", TESTUSERNAME);
        map.add("password", TESTPASSWORD);
        map.add("email", TESTEMAIL);
        this.webTestClient
                .post()
                .uri("/auth/register")
                .body(BodyInserters.fromMultipartData(map))
                .header(ACCEPT,APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isOk();

        //login to get the jwt
        map.clear();
        map.add("username", TESTUSERNAME);
        map.add("password", TESTPASSWORD);

         this.token = this.webTestClient
                .post()
                .uri("/auth/login").accept(MediaType.TEXT_PLAIN)
                .body(BodyInserters.fromMultipartData(map))
                .header(ACCEPT,APPLICATION_JSON_VALUE)
                 .exchange()
                 .expectStatus().isOk()
                 .returnResult(String.class)
                 .getResponseBody()
                 .blockFirst();

         //create a test  character
        ByteArrayResource resource = new MultiPartResource(image.getBytes(), "image.jpg");
        map.clear();
        map.add("name", "Carol Danvers");
        map.add("age", 23);
        map.add("story", "Also known as CaptainMarvel");
        map.add("weight", 65);
        map.add("multipartImage", resource);


        this.webTestClient
                .post()
                .uri("/characters/add")
                .body(BodyInserters.fromMultipartData(map))
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))

                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo("Character created successfully!");
        //characterId = obtainAnyValidCharacterId();
        //create a  test movie
        map.clear();
        map.add("title", "Captain Marvel");
        map.add("rating", 4);
        map.add("releaseYear", 2019);
        map.add("multipartImage", resource);

        this.webTestClient
                .post()
                .uri("/movies/add")
                .body(BodyInserters.fromMultipartData(map))
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))

                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo("Movie  Captain Marvel created successfully");
        // create a second test movie
        map.clear();
        map.add("title", "Ironman");
        map.add("rating", 4);
        map.add("releaseYear", 2008);
        map.add("multipartImage", resource);

        this.webTestClient
                .post()
                .uri("/movies/add")
                .body(BodyInserters.fromMultipartData(map))
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))

                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo("Movie  Ironman created successfully");
        //create a test genre
        map.clear();
        map.add("name", "Fantasy");
        map.add("multipartImage", resource);

        this.webTestClient
                .post()
                .uri("/genres/add")
                .body(BodyInserters.fromMultipartData(map))
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))

                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo("Fantasy Saved");
    }
    @Test
    public void createAccountWithInvalidData(){
        LinkedMultiValueMap map = new LinkedMultiValueMap();
        map.add("username", "J");
        map.add("password", "juan1");
        map.add("email", "juan1@email.com");
        this.webTestClient
                .post()
                .uri("/auth/register")
                .body(BodyInserters.fromMultipartData(map))
                .header(ACCEPT,APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isBadRequest();

        map.clear();
        map.add("username", "Juan1");
        map.add("password", "j");
        map.add("email", "juan1@email.com");
        this.webTestClient
                .post()
                .uri("/auth/register")
                .body(BodyInserters.fromMultipartData(map))
                .header(ACCEPT,APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }
    @Test
    public void loginWithIncorrectCredentials(){
        LinkedMultiValueMap map = new LinkedMultiValueMap();
        map.add("username", "asdasda");
        map.add("password", "faasdfafaan");
        map.add("email", "asdasds@email.com");
        Assertions.assertTrue(map.containsKey("email"));
        this.webTestClient
                .post()
                .uri("/auth/login")
                .body(BodyInserters.fromMultipartData(map))
                .header(ACCEPT,APPLICATION_JSON_VALUE)
                .exchange().expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo("Incorrect username and/or password. try again");

    }
    @Test
    public void accessingProtectedResourceWithoutAuth() throws IOException {
        ByteArrayResource resource = new MultiPartResource(image.getBytes(), "image.jpg");
        LinkedMultiValueMap map = new LinkedMultiValueMap();
        map.add("name", "Carol Danvers");
        map.add("age", 34);
        map.add("story", "Also known as CaptainMarvel");
        map.add("weight", 70);
        map.add("multipartImage", resource);


        this.webTestClient
                .post()
                .uri("/characters/add")
                .body(BodyInserters.fromMultipartData(map))
                .header(ACCEPT,APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isForbidden();

    }
    @Test
    public void AccessingProtectedResourceWithTokenWorks() {
        this.webTestClient
                .get()
                .uri("/characters")
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus()
                .isOk();
    }
    @Test
    public void createCharacterWithMissingFields() throws IOException {
        ByteArrayResource resource = new MultiPartResource(image.getBytes(), "image.jpg");
        LinkedMultiValueMap map = new LinkedMultiValueMap();
        map.add("name", "Carol Danvers");
        map.add("age", 35);
        map.add("multipartImage", resource);


        this.webTestClient
                .post()
                .uri("/characters/add")
                .body(BodyInserters.fromMultipartData(map))
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))

                .exchange()
                .expectStatus()
                .isBadRequest();

    }
    @Test
    public void createCharacterWithInvalidData() throws IOException {
        // with invalid age
        ByteArrayResource resource = new MultiPartResource(image.getBytes(), "image.jpg");
        LinkedMultiValueMap map = new LinkedMultiValueMap();
        map.add("name", "Carol Danvers");
        map.add("age", -55);
        map.add("story", "Also known as CaptainMarvel");
        map.add("weight", 65);
        map.add("multipartImage", resource);


        this.webTestClient
                .post()
                .uri("/characters/add")
                .body(BodyInserters.fromMultipartData(map))
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))

                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .isEqualTo("Age can not be a negative number!");
        // with invalid weight
        map.set("age", 25);
        map.set("weight", -65);



        this.webTestClient
                .post()
                .uri("/characters/add")
                .body(BodyInserters.fromMultipartData(map))
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))

                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .isEqualTo("Weight can not be a negative number!");
    }
    @Test
    public void editACharacterWithoutAuth() throws IOException {
        ByteArrayResource resource = new MultiPartResource(image.getBytes(), "image.jpg");

        LinkedMultiValueMap map = new LinkedMultiValueMap();
        map.add("id",1);
        map.add("name", "Carol Danvers");

        this.webTestClient
                .post()
                .uri("/characters/edit")
                .body(BodyInserters.fromMultipartData(map))
                .header(ACCEPT,APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isForbidden();
    }
    @Test
    public void editACharacterWithInvalidData() throws IOException {
        //with invalid age
        LinkedMultiValueMap map = new LinkedMultiValueMap();
        map.add("id",obtainAnyValidCharacterId());
        map.add("age", -5);

        this.webTestClient
                .post()
                .uri("/characters/edit")
                .body(BodyInserters.fromMultipartData(map))
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))

                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .isEqualTo("Age can not be a negative number!");
        //With invalid weight
        map.set("age", 25);
        map.add("weight", -65);


        this.webTestClient
                .post()
                .uri("/characters/edit")
                .body(BodyInserters.fromMultipartData(map))
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))

                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .isEqualTo("Weight can not be a negative number!");
    }
    @Test
    public void editCharacterWithMissingRequiredParameterId(){
        LinkedMultiValueMap map = new LinkedMultiValueMap();
        map.add("age", 10);

        this.webTestClient
                .post()
                .uri("/characters/edit")
                .body(BodyInserters.fromMultipartData(map))
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))

                .exchange()
                .expectStatus()
                .isBadRequest();
    }
    @Test
    public void editCharacterWithAuthWorks(){
        Long characterId = obtainAnyValidCharacterId();
        String newName = "new edited name";
        Integer newAge =  (int) ((Math.random() * (100 - 1)) + 1);
        Integer newWeight = (int) ((Math.random() * (100 - 1)) + 1);
        String newStory = "new edited story";
        LinkedMultiValueMap map = new LinkedMultiValueMap();
        map.add("id", characterId);
        map.add("name", newName);
        map.add("age", newAge);
        map.add("weight", newWeight);
        map.add("story", newStory);

        this.webTestClient
                .post()
                .uri("/characters/edit")
                .body(BodyInserters.fromMultipartData(map))
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))

                .exchange()
                .expectStatus()
                .isOk();

        this.webTestClient
                .get()
                .uri("/characters/detail?id="+characterId)
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))

                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("name").isEqualTo(newName)
                .jsonPath("age").isEqualTo(newAge)
                .jsonPath("story").isEqualTo(newStory)
                .jsonPath("weight").isEqualTo(newWeight);

    }
    @Test
    public void deleteCharacterWithoutAuth(){
        this.webTestClient
                .delete()
                .uri("/characters/delete?id="+1)
                .header(ACCEPT,APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isForbidden();
    }
    @Test
    public void deleteCharacterWithAuthWorks(){
        Long characterId = obtainAnyValidCharacterId();
        this.webTestClient
                .delete()
                .uri("/characters/delete?id="+characterId)
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo("deleted!");

        this.webTestClient
                .get()
                .uri("/characters/detail?id="+characterId)
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .isEqualTo("Character doesn't exist");
    }
    @Test
    public void filterByWithoutParamsReturnsAllCharacters(){
        long numberOfCharacters = characterRepository.count();
       long size = this.webTestClient
                .get()
                .uri("/characters/")
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Iterable.class)
               .returnResult().getResponseBody().spliterator().getExactSizeIfKnown();
        Assertions.assertEquals(numberOfCharacters, size);

    }
    @Test
    public void addAMovieToACharacterWorks(){
        Long characterId = obtainAnyValidCharacterId();
        //check that the character doesn't have any movies
        this.webTestClient
                .get()
                .uri("/characters/detail?id="+characterId)
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))

                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("movies").isEmpty();

        Long movieId = obtainAnyValidMovieId();

        LinkedMultiValueMap map = new LinkedMultiValueMap();
        map.add("id", characterId);
        map.add("moviesIds", movieId);
        // edit the character to add a movie.
        this.webTestClient
                .post()
                .uri("/characters/edit")
                .body(BodyInserters.fromMultipartData(map))
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))

                .exchange()

                .expectBody(String.class)
                .isEqualTo("Character updated successfully");
        //check that the character has a movie now.
        this.webTestClient
                .get()
                .uri("/characters/detail?id="+characterId)
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))

                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("movies").isNotEmpty();
    }
    @Test
    public void createMovieWithMissingFields() throws IOException {
        ByteArrayResource resource = new MultiPartResource(image.getBytes(), "image.jpg");
        LinkedMultiValueMap map = new LinkedMultiValueMap();
        map.add("title", "Avengers");
        map.add("releaseYear", 2012);
        map.add("multipartImage", resource);


        this.webTestClient
                .post()
                .uri("/movies/add")
                .body(BodyInserters.fromMultipartData(map))
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))

                .exchange()
                .expectStatus()
                .isBadRequest();

    }
    @Test
    public void createMovieWithInvalidData() throws IOException {
        // with invalid releaseYear
        ByteArrayResource resource = new MultiPartResource(image.getBytes(), "image.jpg");
        LinkedMultiValueMap map = new LinkedMultiValueMap();
        map.add("title", "Avengers");
        map.add("releaseYear", 1492);
        map.add("rating", 4);
        map.add("multipartImage", resource);


        this.webTestClient
                .post()
                .uri("/movies/add")
                .body(BodyInserters.fromMultipartData(map))
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))

                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .isEqualTo("Movie release year can't be prior to 1900");
        // with invalid rating
        map.set("rating", 6);
        map.set("releaseYear", 2012);



        this.webTestClient
                .post()
                .uri("/movies/add")
                .body(BodyInserters.fromMultipartData(map))
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))

                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .isEqualTo("Movie rating has to be a value between 1 and 5");
    }
    @Test
    public void editAMovieWithoutAuth() throws IOException {

        LinkedMultiValueMap map = new LinkedMultiValueMap();
        map.add("id",1);
        map.add("title", "Avengers2");

        this.webTestClient
                .post()
                .uri("/movies/edit")
                .body(BodyInserters.fromMultipartData(map))
                .header(ACCEPT,APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isForbidden();
    }
    @Test
    public void editAMovieWithInvalidData() throws IOException {
        // with invalid releaseYear
        ByteArrayResource resource = new MultiPartResource(image.getBytes(), "image.jpg");
        LinkedMultiValueMap map = new LinkedMultiValueMap();
        map.add("id", obtainAnyValidMovieId());
        map.add("releaseYear", 1492);

        this.webTestClient
                .post()
                .uri("/movies/edit")
                .body(BodyInserters.fromMultipartData(map))
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))

                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .isEqualTo("Movie release year can't be prior to 1900");
        // with invalid rating
        map.set("rating", 6);
        map.set("releaseYear", 2012);



        this.webTestClient
                .post()
                .uri("/movies/edit")
                .body(BodyInserters.fromMultipartData(map))
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))

                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .isEqualTo("Movie rating has to be a value between 1 and 5");
    }
    @Test
    public void editMovieWithMissingRequiredParameterId(){
        LinkedMultiValueMap map = new LinkedMultiValueMap();
        map.add("rating", 5);

        this.webTestClient
                .post()
                .uri("/movies/edit")
                .body(BodyInserters.fromMultipartData(map))
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))

                .exchange()
                .expectStatus()
                .isBadRequest();
    }
    @Test
    public void editMovieWithAuthWorks(){
        Long movieId = obtainAnyValidMovieId();
        String newTitle = "new edited title";
        Integer newRating =  (int) ((Math.random() * (5 - 1)) + 1);
        Integer newReleaseYear = 1999;

        LinkedMultiValueMap map = new LinkedMultiValueMap();
        map.add("id", movieId);
        map.add("title", newTitle);
        map.add("releaseYear", newReleaseYear);
        map.add("rating", newRating);


        this.webTestClient
                .post()
                .uri("/movies/edit")
                .body(BodyInserters.fromMultipartData(map))
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo("Movie with Id: " + movieId +" updated successfully");

        this.webTestClient
                .get()
                .uri("/movies/detail?id="+movieId)
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))

                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("title").isEqualTo(newTitle)
                .jsonPath("rating").isEqualTo(newRating)
                .jsonPath("releaseYear").isEqualTo(newReleaseYear);


    }
    @Test
    public void deleteMovieWithoutAuth(){
        this.webTestClient
                .delete()
                .uri("/movies/delete?id="+1)
                .header(ACCEPT,APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isForbidden();
    }
    @Test
    public void deleteMovieWithAuthWorks(){
        Long movieId = obtainAnyValidMovieId();
        this.webTestClient
                .delete()
                .uri("/movies/delete?id="+movieId)
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo("deleted!");

        this.webTestClient
                .get()
                .uri("/movies/detail?id="+movieId)
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .isEqualTo("Movie with Id: "+ movieId +" doesn't exist");
    }
    @Test
    public void deletingAGenreDoesNotDeleteAMovieAssociatedToIt(){
        //edit the movie to associate a genre
        Long movieId = obtainAnyValidMovieId();
        Long genreId = obtainAnyValidGenreId();

        LinkedMultiValueMap map = new LinkedMultiValueMap();
        map.add("id", movieId);
        map.add("genresIds", genreId);


        this.webTestClient
                .post()
                .uri("/movies/edit")
                .body(BodyInserters.fromMultipartData(map))
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo("Movie with Id: " + movieId +" updated successfully");
        //check that the genre was associated successfully
        this.webTestClient
                .get()
                .uri("/movies/detail?id="+movieId)
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("genres").isNotEmpty();

        //delete the genre
        this.webTestClient
                .delete()
                .uri("/genres/delete?id="+genreId)
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo("deleted!");

        //check that the genre was deleted successfully
        this.webTestClient
                .get()
                .uri("/genres/detail?id="+genreId)
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .isEqualTo("Genre doesn't exist");

        //check that the movie survived the genre deletion.
        this.webTestClient
                .get()
                .uri("/movies/detail?id="+movieId)
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("id").isEqualTo(movieId)
                .jsonPath("genres").isEmpty();

    }
    @Test
    public void deletingAMovieDoesNotDeleteAGenreAssociatedToIt(){

        //edit the movie to associate a genre
        Long movieId = obtainAnyValidMovieId();
        Long genreId = obtainAnyValidGenreId();

        LinkedMultiValueMap map = new LinkedMultiValueMap();
        map.add("id", movieId);
        map.add("genresIds", genreId);


        this.webTestClient
                .post()
                .uri("/movies/edit")
                .body(BodyInserters.fromMultipartData(map))
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo("Movie with Id: " + movieId +" updated successfully");
        //check that the genre was associated successfully
        this.webTestClient
                .get()
                .uri("/movies/detail?id="+movieId)
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("genres").isNotEmpty();
        //delete the movie
        this.webTestClient
                .delete()
                .uri("/movies/delete?id="+movieId)
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo("deleted!");
        //check that the movie was deleted successfully
        this.webTestClient
                .get()
                .uri("/movies/detail?id="+movieId)
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .isEqualTo("Movie with Id: "+ movieId +" doesn't exist");
        //check that the genre still exists
        this.webTestClient
                .get()
                .uri("/genres/detail?id="+genreId)
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("id").isEqualTo(genreId);

    }
    private Long obtainAnyValidCharacterId(){
        Iterable<Character> character = characterRepository.findAll();
        Iterator<Character> it = character.iterator();
        long id = 1;
        while (it.hasNext()){
            id = it.next().getId();
        }
        return id;
    }
    private Long obtainAnyValidMovieId(){
        Iterable<Movie> movie = movieRepository.findAll();
        Iterator<Movie> it = movie.iterator();
        long id = 1;
        while (it.hasNext()){
            id = it.next().getId();
        }
        return id;
    }
    private Long obtainAnyValidGenreId(){
        Iterable<Genre> genres = genreRepository.findAll();
        Iterator<Genre> it = genres.iterator();
        long id = 1;
        while (it.hasNext()){
            id = it.next().getId();
        }
        return id;
    }
}
