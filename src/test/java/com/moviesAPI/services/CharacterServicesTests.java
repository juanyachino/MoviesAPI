package com.moviesAPI.services;

import com.moviesAPI.entities.Character;
import com.moviesAPI.entities.Movie;
import com.moviesAPI.exceptions.InvalidAgeException;
import com.moviesAPI.exceptions.InvalidWeightException;
import com.moviesAPI.repositories.CharacterRepository;
import com.moviesAPI.repositories.MovieRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;


import static org.mockito.Mockito.when;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class CharacterServicesTests {
    @InjectMocks //creates a mock implementation of this class and injects the dependent mocks that are marked with
    // the annotations @Mock into it.
    CharacterServices characterServices;

    @Mock //creates a mock implementation for the class
    CharacterRepository characterRepository;

    @Mock //creates a mock implementation for the class
    MovieRepository movieRepository;

    MultipartFile image = new MockMultipartFile("image",new byte[32]);

    @Test
    public void createCharacterTest() throws IOException, InvalidAgeException, InvalidWeightException {

        long precount = characterRepository.count();
        characterServices.createCharacter("TEST", "test", 123, 65,
                new ArrayList<>(),image);
        when(characterRepository.count()).thenReturn(Long.valueOf(1));
        Assertions.assertEquals(precount + 1 , characterRepository.count() );

    }
    @Test
    public void editCharacterWorks() throws IOException, InvalidAgeException, InvalidWeightException {
        when(characterRepository.findById(1L)).thenReturn(
                java.util.Optional.of(new Character(image.getBytes(), "String name", 30, 65, "String story")));

        characterServices.editCharacter(1L,"newName","new story",40,55,new ArrayList<>(),image);
        Character character = characterRepository.findById(1L).get();

        Assertions.assertEquals(character.getName() ,"newName");
        Assertions.assertEquals(character.getStory() ,"new story");
        Assertions.assertEquals(character.getAge() ,Integer.valueOf(40));
        Assertions.assertEquals(character.getWeight() ,Integer.valueOf(55));
        Assertions.assertEquals(character.getImage() ,image.getBytes());



    }
    @Test
    public void getFilteredListReturnsCorrectValues() throws IOException {
        when(characterRepository.findByAge(45)).thenReturn(
                Collections.singleton(new Character(image.getBytes(), "String name", 45, 65, "String story")));
        Assertions.assertEquals(characterServices.getFilteredCharacterList(
                45,null,null,null).spliterator().getExactSizeIfKnown(), 1);

        when(characterRepository.findByWeight(65)).thenReturn(
                Collections.singleton(new Character(image.getBytes(), "name", 45, 65, "story")));
        Assertions.assertEquals(characterServices.getFilteredCharacterList(
                null,65,null,null).spliterator().getExactSizeIfKnown(), 1);

        when(characterRepository.findByName("name")).thenReturn(
                Collections.singleton(new Character(image.getBytes(), "name", 45, 65, "story")));
        Assertions.assertEquals(characterServices.getFilteredCharacterList(
                null,null,"name",null).spliterator().getExactSizeIfKnown(), 1);

        Movie mockMovie = org.mockito.Mockito.mock(Movie.class);
        //Movie movie = new Movie(image.getBytes(), "title", "date", 5);
        when(movieRepository.findById(1L)).thenReturn(
                java.util.Optional.of(mockMovie));
        when(mockMovie.getCharacters()).thenReturn(
                Collections.singleton(new Character(image.getBytes(), "name", 45, 65, "story")));

        Assertions.assertEquals(characterServices.getFilteredCharacterList(
                null,null,null, 1L).spliterator().getExactSizeIfKnown(), 1);
    }
    @Test
    public void deleteCharacterThatDoesNotExist(){
        Assertions.assertEquals(0 , characterRepository.count() );
        Assertions.assertFalse(characterServices.deleteCharacter(1L));
    }
    @Test
    public void deleteCharacter() throws IOException {
        when(characterRepository.findById(1L)).thenReturn(
                java.util.Optional.of(new Character()));
        Assertions.assertTrue(characterServices.deleteCharacter(1L));
        Assertions.assertEquals(characterRepository.count(), 0);

    }
}
