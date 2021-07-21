package com.moviesAPI.moviesAPI;

import com.moviesAPI.moviesAPI.repositories.UserRepository;
import com.moviesAPI.moviesAPI.utils.MultiPartResource;
import io.jsonwebtoken.Jwt;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.JsonPathAssertions;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClientConfigurer;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;





import java.io.IOException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.*;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MoviesApiIntegrationTests {

    @Autowired
    private WebTestClient webTestClient;

    //@MockBean
    //private UserRepository userRepository; //can't get a token if using this.

    MultipartFile image = new MockMultipartFile("image",new byte[12]);


    String token = "";


    @Before
    public void accountCreation() throws IOException {

        LinkedMultiValueMap map = new LinkedMultiValueMap();
        map.add("username", "juan123");
        map.add("password", "juan123");
        map.add("email", "juan123@email.com");
        this.webTestClient
                .post()
                .uri("/auth/register")
                .body(BodyInserters.fromMultipartData(map))
                .header(ACCEPT,APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isOk();


        /*map = new LinkedMultiValueMap();
        map.add("username", "juan123");
        map.add("password", "juan123");

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
 */
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
    public void accessProtectedResourceWithoutAuth() throws IOException {
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
    public void createCharacterWithInvalidAge() throws IOException {
        /*LinkedMultiValueMap map = new LinkedMultiValueMap();
        map.add("username", "juan123");
        map.add("password", "juan123");

        String AuthToken = this.webTestClient
                .post()
                .uri("/auth/login").accept(MediaType.TEXT_PLAIN)
                .body(BodyInserters.fromMultipartData(map))
                .header(ACCEPT,APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isOk()
                .returnResult(String.class)
                .getResponseBody()
                .blockFirst(); */
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
                .headers(httpHeaders -> httpHeaders.setBearerAuth(loginAndGetToken("juan123","juan123")))

                .exchange()
                .expectStatus()
                .isBadRequest();

    }
    @Test
    public void AccessingProtectedResourceWithTokenWorks() {
        this.webTestClient
                .get()
                .uri("/characters")
                .header(AUTHORIZATION,ACCEPT,APPLICATION_JSON_VALUE)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(loginAndGetToken("juan123","juan123")))
                .exchange()
                .expectStatus()
                .isOk();
    }
    private String loginAndGetToken(String username, String password){
        LinkedMultiValueMap map = new LinkedMultiValueMap();
        map.add("username", username);
        map.add("password", password);

        return this.webTestClient
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
}
