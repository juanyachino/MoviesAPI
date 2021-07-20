package com.moviesAPI.moviesAPI;

import com.moviesAPI.moviesAPI.utils.MultiPartResource;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MoviesApiIntegrationTests {
    @Autowired
    private WebTestClient webTestClient;

    MultipartFile image = new MockMultipartFile("image",new byte[12]);

    String token = new String();
    @Before
    public void accountCreationAndLogin(){
        LinkedMultiValueMap map = new LinkedMultiValueMap();
        map.add("username", "Juan123");
        map.add("password", "juan123");
        map.add("email", "juan123@email.com");
        this.webTestClient
                .post()
                .uri("/auth/register")
                .body(BodyInserters.fromMultipartData(map))
                .header(ACCEPT,APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .is2xxSuccessful();

        map = new LinkedMultiValueMap();
        map.add("username", "Juan123");
        map.add("password", "juan123");

        token = this.webTestClient
                .post()
                .uri("/auth/login")
                .body(BodyInserters.fromMultipartData(map))
                .header(ACCEPT,APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody().toString();
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
                .is4xxClientError();

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
                .is4xxClientError();
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
    public void testtest(){
        this.webTestClient
                .get()
                .uri("/auth/register")
                .header(ACCEPT,APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectHeader()
                .contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.length()").isEqualTo(3)
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].name").isEqualTo("duke")
                .jsonPath("$[0].tags").isNotEmpty();
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

        /*.mutateWith(mockJwt().authorities(new SimpleGrantedAuthority("authorities")))*/
        this.webTestClient
                .post()
                .uri("/characters/add")
                .body(BodyInserters.fromMultipartData(map))
                .header(ACCEPT,APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .is4xxClientError();
    }
}
