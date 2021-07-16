package com.moviesAPI.moviesAPI.repositories;

import com.moviesAPI.moviesAPI.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


// This will be AUTO IMPLEMENTED by Spring into a Bean called characterRepository
// CRUD refers Create, Read, Update, Delete

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findById(long id);

    void deleteById(long id);

    Optional<User> findByEmail(String email);
}