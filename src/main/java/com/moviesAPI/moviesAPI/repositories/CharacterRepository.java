package com.moviesAPI.moviesAPI.repositories;

import org.springframework.data.repository.CrudRepository;
import com.moviesAPI.moviesAPI.entities.Character;

// This will be AUTO IMPLEMENTED by Spring into a Bean called characterRepository
// CRUD refers Create, Read, Update, Delete

public interface CharacterRepository extends CrudRepository<Character, Integer> {

}
