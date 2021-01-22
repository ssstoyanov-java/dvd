package io.ssstoyanov.dvd2.controllers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import io.ssstoyanov.dvd2.configurations.View;
import io.ssstoyanov.dvd2.entities.User;
import io.ssstoyanov.dvd2.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/users")
@RestController
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder bCryptPasswordEncoder;


    public UserController(UserRepository userRepository,
                          PasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @JsonIgnore
    @JsonView(View.Internal.class)
    @Operation(summary = "User sign up")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully created"),
            @ApiResponse(responseCode = "409", description = "The user already exist")
    })
    @Transactional
    @RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<?> userCreation(@RequestBody User user) {
        System.out.println(user);
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } else {
            userRepository.save(new User(new ObjectId(), user.getUsername(), bCryptPasswordEncoder.encode(user.getPassword()), null));
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

}
