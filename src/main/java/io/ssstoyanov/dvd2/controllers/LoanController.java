package io.ssstoyanov.dvd2.controllers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import io.ssstoyanov.dvd2.configurations.View;
import io.ssstoyanov.dvd2.entities.Disk;
import io.ssstoyanov.dvd2.entities.User;
import io.ssstoyanov.dvd2.repositories.DiskRepository;
import io.ssstoyanov.dvd2.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

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

    @JsonIgnore
    @JsonView(View.Public.class)
    @Operation(
            summary = "Rent a disc",
            description = "Rent a disk from a user if it is not busy"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful loan"),
            @ApiResponse(responseCode = "404", description = "User did not found by name"),
            @ApiResponse(responseCode = "409", description = "Disk already taken")
    })
    @Transactional
    @RequestMapping(value = "/loan/take", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> takeLoan(Disk disk, String username) {
        disk = diskRepository.findByName(disk.getName()).get();
        if (!disk.getOriginalOwner().equals(userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (disk.getCurrentOwner() != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } else {
            User currentUser = user.get();
            disk.setCurrentOwner(currentUser);
            currentUser.getDisks().add(disk);
            userRepository.save(currentUser);
            diskRepository.save(disk);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @JsonIgnore
    @JsonView(View.Public.class)
    @Operation(
            summary = "Give a disc out of rent",
            description = "The disk is returned to the user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful pay"),
            @ApiResponse(responseCode = "404", description = "User did not found by name"),
            @ApiResponse(responseCode = "409", description = "Disk already free")

    })
    @Transactional
    @RequestMapping(value = "/loan/pay", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> payLoan(Disk disk) {
        disk = diskRepository.findByName(disk.getName()).get();
        User user = disk.getCurrentOwner();
        user.getDisks().remove(disk);
        disk.setCurrentOwner(null);
        userRepository.save(user);
        diskRepository.save(disk);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
