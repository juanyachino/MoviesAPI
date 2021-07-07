package com.moviesAPI.moviesAPI.repositories;


import org.springframework.data.repository.CrudRepository;
import com.moviesAPI.moviesAPI.entities.Character;

import java.util.List;

// This will be AUTO IMPLEMENTED by Spring into a Bean called characterRepository
// CRUD refers Create, Read, Update, Delete

public interface CharacterRepository extends CrudRepository<Character, Long> {
    List<Character> findByName(String name);

    Iterable<Character> findByAge(Integer age);

    Iterable<Character> findByWeight(Integer weight);
}
