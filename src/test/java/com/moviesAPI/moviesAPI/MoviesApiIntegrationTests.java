package com.moviesAPI.moviesAPI;

import com.moviesAPI.moviesAPI.utils.MultiPartResource;

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


    MultipartFile image = new MockMultipartFile("image",new byte[12]);


    String token = "";


    @BeforeAll
    public void accountCreation()  {

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


        map = new LinkedMultiValueMap();
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

        map = new LinkedMultiValueMap();
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
        this.webTestClient
                .post()
                .uri("/auth/login")
                .body(BodyInserters.fromMultipartData(map))
                .header(ACCEPT,APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .toString()
                .equalsIgnoreCase("Incorrect username and/or password. try again");
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
    public void createCharacterWithInvalidAge() throws IOException {

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
                .isBadRequest();

    }
    @Test
    public void createCharacterWithInvalidWeight() throws IOException {
        ByteArrayResource resource = new MultiPartResource(image.getBytes(), "image.jpg");
        LinkedMultiValueMap map = new LinkedMultiValueMap();
        map.add("name", "Carol Danvers");
        map.add("age", 25);
        map.add("story", "Also known as CaptainMarvel");
        map.add("weight", -65);
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
}
