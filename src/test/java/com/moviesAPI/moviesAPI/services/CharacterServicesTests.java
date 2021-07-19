package com.moviesAPI.moviesAPI.services;

import com.moviesAPI.moviesAPI.entities.Character;
import com.moviesAPI.moviesAPI.entities.Movie;
import com.moviesAPI.moviesAPI.repositories.CharacterRepository;
import com.moviesAPI.moviesAPI.repositories.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.Assert.*;
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
    public void createCharacterTest() throws IOException {

        long precount = characterRepository.count();
        characterServices.createCharacter("TEST", "test", 123, 65,
                new ArrayList<>(),image);
        when(characterRepository.count()).thenReturn(Long.valueOf(1));
        assertEquals(precount + 1 , characterRepository.count() );

    }
    @Test
    public void editCharacterWorks() throws IOException {
        when(characterRepository.findById(Long.valueOf(1))).thenReturn(
                java.util.Optional.of(new Character(image.getBytes(), "String name", 30, 65, "String story")));

        characterServices.editCharacter(Long.valueOf(1),"newName","new story",40,55,new ArrayList<>(),image);
        Character character = characterRepository.findById(Long.valueOf(1)).get();

        assertEquals(character.getName() ,"newName");
        assertEquals(character.getStory() ,"new story");
        assertEquals(character.getAge() ,Integer.valueOf(40));
        assertEquals(character.getWeight() ,Integer.valueOf(55));
        assertEquals(character.getImage() ,image.getBytes());



    }
    @Test
    public void getFilteredListReturnsCorrectValues() throws IOException {
        when(characterRepository.findByAge(45)).thenReturn(
                Collections.singleton(new Character(image.getBytes(), "String name", 45, 65, "String story")));
        assertEquals(characterServices.getFilteredCharacterList(
                45,null,null,null).spliterator().getExactSizeIfKnown(), 1);

        when(characterRepository.findByWeight(65)).thenReturn(
                Collections.singleton(new Character(image.getBytes(), "name", 45, 65, "story")));
        assertEquals(characterServices.getFilteredCharacterList(
                null,65,null,null).spliterator().getExactSizeIfKnown(), 1);

        when(characterRepository.findByName("name")).thenReturn(
                Collections.singleton(new Character(image.getBytes(), "name", 45, 65, "story")));
        assertEquals(characterServices.getFilteredCharacterList(
                null,null,"name",null).spliterator().getExactSizeIfKnown(), 1);

        Movie mockMovie = org.mockito.Mockito.mock(Movie.class);
        //Movie movie = new Movie(image.getBytes(), "title", "date", 5);
        when(movieRepository.findById(Long.valueOf(1))).thenReturn(
                java.util.Optional.of(mockMovie));
        when(mockMovie.getCharacters()).thenReturn(
                Collections.singleton(new Character(image.getBytes(), "name", 45, 65, "story")));

        assertEquals(characterServices.getFilteredCharacterList(
                null,null,null, Long.valueOf(1)).spliterator().getExactSizeIfKnown(), 1);
    }
    @Test
    public void deleteCharacterThatDoesNotExist(){
        assertEquals(0 , characterRepository.count() );
        assertFalse(characterServices.deleteCharacter(Long.valueOf(1)));
    }
    @Test
    public void deleteCharacter() throws IOException {
        when(characterRepository.findById(Long.valueOf(1))).thenReturn(
                java.util.Optional.of(new Character()));
        assertTrue(characterServices.deleteCharacter(Long.valueOf(1)));
        assertEquals(characterRepository.count(), 0);

    }
}
