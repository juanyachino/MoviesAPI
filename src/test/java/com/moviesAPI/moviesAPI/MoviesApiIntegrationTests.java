package com.moviesAPI.moviesAPI;

import com.moviesAPI.entities.Character;
import com.moviesAPI.repositories.CharacterRepository;
import com.moviesAPI.repositories.GenreRepository;
import com.moviesAPI.repositories.MovieRepository;
import com.moviesAPI.utils.MultiPartResource;


import org.assertj.core.api.Assertions;
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
    private Long characterId;
    private Long movieId;
    private Long genreId;


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
        characterId = obtainAnyValidCharacterId();
        //create a  test movie

        //create a test genre
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
        Assertions.assertThat(map.containsKey("email"));
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
                .isEqualTo("Character's age is invalid!");
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
                .isEqualTo("Character's weight is invalid!");
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
                .isEqualTo("Character's age is invalid!");
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
                .isEqualTo("Character's weight is invalid!");
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
        this.webTestClient
                .delete()
                .uri("/characters/delete?id="+obtainAnyValidCharacterId())
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo("deleted!");
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
}
