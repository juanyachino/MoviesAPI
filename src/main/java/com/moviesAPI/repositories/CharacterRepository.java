package com.moviesAPI.repositories;


import org.springframework.data.repository.CrudRepository;
import com.moviesAPI.entities.Character;

// This will be AUTO IMPLEMENTED by Spring into a Bean called characterRepository
// CRUD refers Create, Read, Update, Delete

public interface CharacterRepository extends CrudRepository<Character, Long> {
    Iterable<Character> findByName(String name);

    Iterable<Character> findByAge(Integer age);

    Iterable<Character> findByWeight(Integer weight);

    Iterable<CharactersListView> findBy();
    interface CharactersListView {

        byte[] getImage();
        String getName();
    }

}

