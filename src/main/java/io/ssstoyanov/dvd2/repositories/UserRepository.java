package io.ssstoyanov.dvd2.repositories;

import io.ssstoyanov.dvd2.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, Long> {
    Optional<User> findByUsername(String username);

    void deleteUserByUsername(String username);
}
