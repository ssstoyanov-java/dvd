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
            @ApiResponse(responseCode = "404", description = "User or disk did not found by name"),
            @ApiResponse(responseCode = "409", description = "Disk already taken")
    })
    @Transactional
    @RequestMapping(value = "/loan/take", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> takeLoan(Disk disk, String username) {
        Optional<Disk> optionalDisk = diskRepository.findByName(disk.getName());
        Optional<User> user = userRepository.findByUsername(username);
        if (optionalDisk.isEmpty()) {        // check disk is correct
            return new ResponseEntity<>("Disk not found", HttpStatus.NOT_FOUND);
        } else if (optionalDisk.get().getCurrentOwner() != null) {         // check the disk is not already taken
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } else if (!optionalDisk.get().getOriginalOwner()
                .equals(userRepository
                        .findByUsername(SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getName()).orElse(null))) {         // check the user have owner rights
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else if (user.isEmpty()) {    // check the user to does exist
            return new ResponseEntity<>("The user not found", HttpStatus.NOT_FOUND);
        } else {
            disk = optionalDisk.get();
            User currentUser = user.get();
            disk.setCurrentOwner(currentUser);
            currentUser.getDisks().add(disk);
            User originalOwner = disk.getOriginalOwner();
            originalOwner.getDisks().remove(disk);
            userRepository.save(originalOwner);
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
    public ResponseEntity<String> payLoan(Disk disk) {
        Optional<Disk> optionalDisk = diskRepository.findByName(disk.getName());
        if (optionalDisk.isEmpty()) {         // check disk is correct
            return new ResponseEntity<>("Disk not found", HttpStatus.NOT_FOUND);
        } else if (disk.getCurrentOwner() != null) {         // check the disk is on rent
            return new ResponseEntity<>("Disk on rent", HttpStatus.CONFLICT);
        } else {
            disk = optionalDisk.get();
            User user = disk.getCurrentOwner();
            user.getDisks().remove(disk);
            disk.setCurrentOwner(null);
            userRepository.save(user);
            diskRepository.save(disk);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @JsonIgnore
    @JsonView(View.Public.class)
    @Operation(
            summary = "Give a disc absolutely free",
            description = "The disk is belongs to nobody"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful pay"),
            @ApiResponse(responseCode = "404", description = "User did not found by name"),
            @ApiResponse(responseCode = "409", description = "Disk already free")

    })
    @Transactional
    @RequestMapping(value = "/loan/free", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> freeDisk(Disk disk) {
        Optional<Disk> optionalDisk = diskRepository.findByName(disk.getName());
        if (optionalDisk.isEmpty()) {         // check disk is correct
            return new ResponseEntity<>("Disk not found", HttpStatus.NOT_FOUND);
        } else if (optionalDisk.get().getCurrentOwner() == null) {         // check the disk was taken
            return new ResponseEntity<>("Disk already free", HttpStatus.CONFLICT);
        } else if (!disk.getOriginalOwner()
                .equals(userRepository
                        .findByUsername(SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getName()).orElse(null))) {         // check the user have owner rights
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else {
            disk = optionalDisk.get();
            User user = disk.getCurrentOwner();
            user.getDisks().remove(disk);
            disk.setCurrentOwner(null);
            userRepository.save(user);
            diskRepository.save(disk);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
}
