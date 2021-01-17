package io.ssstoyanov.dvd2.controllers;

import io.ssstoyanov.dvd2.repositories.DiskRepository;
import io.ssstoyanov.dvd2.repositories.UserRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RestController
public class LoanController {

    private final DiskRepository diskRepository;
    private final UserRepository userRepository;

    public LoanController(DiskRepository diskRepository,
                          UserRepository userRepository) {
        this.diskRepository = diskRepository;
        this.userRepository = userRepository;
    }

}
