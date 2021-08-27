package com.moviesAPI.moviesAPI;

import com.moviesAPI.entities.Character;
import com.moviesAPI.entities.Genre;
import com.moviesAPI.entities.Movie;
import com.moviesAPI.entities.User;
import com.moviesAPI.repositories.CharacterRepository;
import com.moviesAPI.repositories.GenreRepository;
import com.moviesAPI.repositories.MovieRepository;
import com.moviesAPI.utils.MultiPartResource;


import net.minidev.json.JSONObject;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties="spring.jpa.hibernate.ddl-auto=create-drop") // clears the database after every test run
@TestPropertySource(properties="spring.datasource.url=jdbc:mysql://${MYSQL_HOST:localhost}:3306/movies_dev") // uses a different database
public class MoviesApiIntegrationTests {
    private static final String TESTUSERNAME = "test1";
    private static final String TESTPASSWORD = "Test123!";
    private static final String TESTEMAIL = "test@gmail.com";
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
        JSONObject userForm = new JSONObject();
        userForm.put("username",TESTUSERNAME);
        userForm.put("password",TESTPASSWORD);
        userForm.put("email",TESTEMAIL);
        this.webTestClient
                .post()
                .uri("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userForm.toJSONString()))
                .header(ACCEPT,APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isOk();

        //login to get the jwt

        this.token = this.webTestClient
                 .post()
                 .uri("/auth/login")
                 .contentType(MediaType.APPLICATION_JSON)
                 .body(BodyInserters.fromValue(userForm.toJSONString()))
                 .header(ACCEPT,APPLICATION_JSON_VALUE)
                 .exchange()
                 .expectStatus()
                 .isOk()
                 .returnResult(String.class)
                 .getResponseBody()
                 .blockFirst();

        //create a test character
        ByteArrayResource resource = new MultiPartResource(image.getBytes(), "image.jpg");

        JSONObject characterForm = new JSONObject();
        characterForm.put("name","Carol Danvers");
        characterForm.put("age",35);
        characterForm.put("weight",60);
        characterForm.put("story","Also known as Captain Marvel");
        map.clear();
        map.add("file",resource);
        map.add("data",characterForm);
        this.webTestClient
                .post()
                .uri("/characters/add")
                .contentType(MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(map))
                .header(ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo("Character created successfully!");


        //create a  test movie

        JSONObject movieForm = new JSONObject();
        movieForm.put("title","Avengers: Endgame");
        movieForm.put("rating",5);
        movieForm.put("releaseYear",2019);

        map.clear();
        map.add("file",resource);
        map.add("data",movieForm);
        this.webTestClient
                .post()
                .uri("/movies/add")
                .contentType(MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(map))
                .header(ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo("Movie  Avengers: Endgame created successfully");

        // create a second test movie
        movieForm = new JSONObject();
        movieForm.put("title","ironman");
        movieForm.put("rating",4);
        movieForm.put("releaseYear",2008);

        map.clear();
        map.add("file",resource);
        map.add("data",movieForm);
        this.webTestClient
                .post()
                .uri("/movies/add")
                .contentType(MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(map))
                .header(ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo("Movie  ironman created successfully");

        //create a test genre
        JSONObject genreForm = new JSONObject();
        genreForm.put("name","Fantasy");

        map.clear();
        map.add("file",resource);
        map.add("data",genreForm);
        this.webTestClient
                .post()
                .uri("/genres/add")
                .contentType(MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(map))
                .header(ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo("Fantasy Saved");
    }
    @Test
    public void createAccountWithInvalidData(){
        User user = new User();
        user.setUsername("j");
        user.setEmail(TESTEMAIL);
        user.setPassword(TESTPASSWORD);
        this.webTestClient
                .post()
                .uri("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(user))
                .header(ACCEPT,APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isBadRequest();

        user.setUsername(TESTUSERNAME);
        user.setPassword("ps");
        this.webTestClient
                .post()
                .uri("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(user))
                .header(ACCEPT,APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }
    @Test
    public void loginWithIncorrectCredentials(){
        User user = new User();
        user.setUsername("asdasdafas");
        user.setEmail("adsadafggasd");
        user.setPassword("asdagadgsfdgd");
        this.webTestClient
                .post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(user))
                .header(ACCEPT,APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
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
        LinkedMultiValueMap map = new LinkedMultiValueMap();
        ByteArrayResource resource = new MultiPartResource(image.getBytes(), "image.jpg");
        JSONObject characterForm = new JSONObject();
        characterForm.put("name","Carol Danvers");
        characterForm.put("age",35);
        characterForm.put("weight",60);
        map.clear();
        map.add("file",resource);
        map.add("data",characterForm);
        this.webTestClient
                .post()
                .uri("/characters/add")
                .contentType(MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(map))
                .header(ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus()
                .isBadRequest();

    }
    @Test
    public void createCharacterWithInvalidData() throws IOException {
        // with invalid age
        LinkedMultiValueMap map = new LinkedMultiValueMap();
        ByteArrayResource resource = new MultiPartResource(image.getBytes(), "image.jpg");
        JSONObject characterForm = new JSONObject();
        characterForm.put("name","Carol Danvers");
        characterForm.put("age",-1);
        characterForm.put("weight",60);
        characterForm.put("story","Captain marvel");
        map.clear();
        map.add("file",resource);
        map.add("data",characterForm);
        this.webTestClient
                .post()
                .uri("/characters/add")
                .contentType(MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(map))
                .header(ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus()
                .isBadRequest();
        //with invalid weight
        characterForm = new JSONObject();
        characterForm.put("name","Carol Danvers");
        characterForm.put("age",49);
        characterForm.put("weight",-1);
        characterForm.put("story","Captain marvel");
        map.clear();
        map.add("file",resource);
        map.add("data",characterForm);
        this.webTestClient
                .post()
                .uri("/characters/add")
                .contentType(MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(map))
                .header(ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus()
                .isBadRequest();
    }
    @Test
    public void editACharacterWithoutAuth() throws IOException {
        ByteArrayResource resource = new MultiPartResource(image.getBytes(), "image.jpg");
        LinkedMultiValueMap map = new LinkedMultiValueMap();
        JSONObject characterForm = new JSONObject();
        characterForm.put("id",1);
        characterForm.put("name","Carol Danvers");
        characterForm.put("age",-1);
        characterForm.put("weight",60);
        characterForm.put("story","Captain marvel");
        map.clear();
        map.add("file",resource);
        map.add("data",characterForm);

        this.webTestClient
                .post()
                .uri("/characters/edit")
                .contentType(MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(map))
                .header(ACCEPT,APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isForbidden();
    }
    @Test
    public void editACharacterWithInvalidData() throws IOException {
        //with invalid age
        ByteArrayResource resource = new MultiPartResource(image.getBytes(), "image.jpg");
        LinkedMultiValueMap map = new LinkedMultiValueMap();
        JSONObject characterForm = new JSONObject();
        characterForm.put("id",1);
        characterForm.put("name","Carol Danvers");
        characterForm.put("age",-1);
        characterForm.put("weight",60);
        characterForm.put("story","Captain marvel");
        map.clear();
        map.add("file",resource);
        map.add("data",characterForm);

        this.webTestClient
                .post()
                .uri("/characters/edit")
                .contentType(MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(map))
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus()
                .isBadRequest();
        //With invalid weight
        characterForm = new JSONObject();
        characterForm.put("id",1);
        characterForm.put("name","Carol Danvers");
        characterForm.put("age",49);
        characterForm.put("weight",-1);
        characterForm.put("story","Captain marvel");
        map.clear();
        map.add("file",resource);
        map.add("data",characterForm);

        this.webTestClient
                .post()
                .uri("/characters/edit")
                .contentType(MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(map))
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))

                .exchange()
                .expectStatus()
                .isBadRequest();
    }
    @Test
    public void editCharacterWithMissingRequiredParameterId() throws IOException {
        ByteArrayResource resource = new MultiPartResource(image.getBytes(), "image.jpg");
        LinkedMultiValueMap map = new LinkedMultiValueMap();
        JSONObject characterForm = new JSONObject();
        characterForm.put("name","Carol Danvers");
        characterForm.put("age",-1);
        characterForm.put("weight",60);
        characterForm.put("story","Captain marvel");
        map.clear();
        map.add("file",resource);
        map.add("data",characterForm);

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
    public void editCharacterWithAuthWorks() throws IOException {
        ByteArrayResource resource = new MultiPartResource(image.getBytes(), "image.jpg");
        JSONObject characterForm = new JSONObject();
        Long characterId = obtainAnyValidCharacterId();
        String newName = "new edited name";
        Integer newAge =  (int) ((Math.random() * (100 - 1)) + 1);
        Integer newWeight = (int) ((Math.random() * (100 - 1)) + 1);
        String newStory = "new edited story";
        characterForm.put("id",characterId);
        characterForm.put("name",newName);
        characterForm.put("age",newAge);
        characterForm.put("weight",newWeight);
        characterForm.put("story",newStory);
        LinkedMultiValueMap map = new LinkedMultiValueMap();
        map.add("file",resource);
        map.add("data",characterForm);
        this.webTestClient
                .post()
                .uri("/characters/edit")
                .contentType(MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(map))
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus()
                .isOk();
        //check if the character was edited successfully.
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
    public void addAMovieToACharacterWorks() throws IOException {
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
        List<Long> moviesIdList = new ArrayList<>();
        moviesIdList.add(movieId);
        ByteArrayResource resource = new MultiPartResource(image.getBytes(), "image.jpg");

        JSONObject characterForm = new JSONObject();
        characterForm.put("id",characterId);
        characterForm.put("moviesIds",moviesIdList);
        LinkedMultiValueMap map = new LinkedMultiValueMap();
        map.add("file",resource);
        map.add("data",characterForm);
        this.webTestClient
                .post()
                .uri("/characters/edit")
                .contentType(MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(map))
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus()
                .isOk();
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
        JSONObject movieForm = new JSONObject();
        movieForm.put("title","Avengers: Endgame");
        movieForm.put("rating",5);
        movieForm.put("releaseYear",1492);
        ByteArrayResource resource = new MultiPartResource(image.getBytes(), "image.jpg");
        LinkedMultiValueMap map = new LinkedMultiValueMap();
        map.add("file",resource);
        map.add("data",movieForm);
        this.webTestClient
                .post()
                .uri("/movies/add")
                .contentType(MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(map))
                .header(ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus()
                .isBadRequest();

        // with greater than 5 rating
        movieForm = new JSONObject();
        movieForm.put("title","Avengers: Endgame");
        movieForm.put("rating",6);
        movieForm.put("releaseYear",1999);
        map.clear();
        map.add("file",resource);
        map.add("data",movieForm);
        this.webTestClient
                .post()
                .uri("/movies/add")
                .contentType(MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(map))
                .header(ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus()
                .isBadRequest();

        // with smaller than 1 rating
        movieForm = new JSONObject();
        movieForm.put("title","Avengers: Endgame");
        movieForm.put("rating",0);
        movieForm.put("releaseYear",1999);
        map.clear();
        map.add("file",resource);
        map.add("data",movieForm);
        this.webTestClient
                .post()
                .uri("/movies/add")
                .contentType(MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(map))
                .header(ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus()
                .isBadRequest();
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
                .expectBody(String.class);
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
                .isBadRequest();
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
    public void editMovieWithAuthWorks() throws IOException {
        LinkedMultiValueMap map = new LinkedMultiValueMap();
        ByteArrayResource resource = new MultiPartResource(image.getBytes(), "image.jpg");
        Long movieId = obtainAnyValidMovieId();
        String newTitle = "new edited title";
        Integer newRating =  (int) ((Math.random() * (5 - 1)) + 1);
        Integer newReleaseYear = 1999;
        JSONObject movieForm = new JSONObject();
        movieForm.put("id",movieId);
        movieForm.put("title",newTitle);
        movieForm.put("rating",newRating);
        movieForm.put("releaseYear",newReleaseYear);
        map.clear();
        map.add("file",resource);
        map.add("data",movieForm);
        this.webTestClient
                .post()
                .uri("/movies/edit")
                .contentType(MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(map))
                .header(ACCEPT,APPLICATION_JSON_VALUE)
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
    public void deletingAGenreDoesNotDeleteAMovieAssociatedToIt() throws IOException {
        //edit the movie to associate a genre
        LinkedMultiValueMap map = new LinkedMultiValueMap();
        ByteArrayResource resource = new MultiPartResource(image.getBytes(), "image.jpg");
        Long movieId = obtainAnyValidMovieId();
        Long genreId = obtainAnyValidGenreId();
        List<Long> genreList = new ArrayList<>();
        genreList.add(genreId);
        JSONObject movieForm = new JSONObject();
        movieForm.put("id",movieId);
        movieForm.put("genresIds",genreList);

        map.clear();
        map.add("file",resource);
        map.add("data",movieForm);
        this.webTestClient
                .post()
                .uri("/movies/edit")
                .contentType(MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(map))
                .header(ACCEPT,APPLICATION_JSON_VALUE)
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
    public void deletingAMovieDoesNotDeleteAGenreAssociatedToIt() throws IOException {

        //edit the movie to associate a genre
        LinkedMultiValueMap map = new LinkedMultiValueMap();
        ByteArrayResource resource = new MultiPartResource(image.getBytes(), "image.jpg");
        Long movieId = obtainAnyValidMovieId();
        Long genreId = obtainAnyValidGenreId();
        List<Long> genreList = new ArrayList<>();
        genreList.add(genreId);
        JSONObject movieForm = new JSONObject();
        movieForm.put("id",movieId);
        movieForm.put("genresIds",genreList);

        map.clear();
        map.add("file",resource);
        map.add("data",movieForm);
        this.webTestClient
                .post()
                .uri("/movies/edit")
                .contentType(MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(map))
                .header(ACCEPT,APPLICATION_JSON_VALUE)
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
