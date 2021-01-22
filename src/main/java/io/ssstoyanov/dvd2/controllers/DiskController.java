package io.ssstoyanov.dvd2.controllers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import io.ssstoyanov.dvd2.configurations.View;
import io.ssstoyanov.dvd2.entities.Disk;
import io.ssstoyanov.dvd2.repositories.DiskRepository;
import io.ssstoyanov.dvd2.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Disk Controller", description = "Provide info about free disks and user's disks")
@RequestMapping("/api/v1")
@RestController
public class DiskController {

    private final DiskRepository diskRepository;
    private final UserRepository userRepository;

    public DiskController(DiskRepository diskRepository,
                          UserRepository userRepository) {
        this.diskRepository = diskRepository;
        this.userRepository = userRepository;
    }

    @JsonIgnore
    @JsonView(View.ExtendedPublic.class)
    @Operation(
            summary = "List of own dvd disks for each user",
            description = "Returns a page-by-page display of the user's dvd disks by its name"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful get"),
            @ApiResponse(responseCode = "404", description = "User did not found by name")
    })
    @Transactional
    @RequestMapping(value = "/disks/{username}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Page<Disk>> getUsersDisks(@PathVariable String username, Pageable pageable) {
        return userRepository.findByUsername(username)
                .map(user -> new ResponseEntity<>(diskRepository.findDiskByOriginalOwner(user, pageable), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @JsonIgnore
    @JsonView(View.ExtendedPublic.class)
    @Operation(
            summary = "List of DVD discs that you can take",
            description = "Returns a page-by-page display of free to take DVDs"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful get"),
    })
    @RequestMapping(value = "/disks", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Page<Disk>> getFreeDisks(Pageable pageable) {
        return new ResponseEntity<>(diskRepository.findDiskByOriginalOwnerIsNull(pageable), HttpStatus.OK);
    }


    @JsonIgnore
    @JsonView(View.ExtendedPublic.class)
    @Operation(
            summary = "List of DVD discs that you can take from other users",
            description = "Returns a page-by-page display of user's free to take DVDs"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful get"),
    })
    @RequestMapping(value = "/disks/free", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Page<Disk>> getFreeUsersDisks(Pageable pageable) {
        return new ResponseEntity<>(diskRepository.findDiskByCurrentOwnerIsNull(pageable), HttpStatus.OK);
    }

    @JsonIgnore
    @JsonView(View.ExtendedPublic.class)
    @Operation(
            summary = "List of disks taken from the user",
            description = "Returns a page-by-page list of disks taken from the user (indicating who took it)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful get"),
            @ApiResponse(responseCode = "404", description = "User did not found by name")
    })
    @Transactional
    @RequestMapping(value = "/disks/busy/{username}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Page<Disk>> getTakenDisksFromUser(@PathVariable String username, Pageable pageable) {
        return userRepository.findByUsername(username)
                .map(user -> new ResponseEntity<>(diskRepository.findDiskByOriginalOwnerAndCurrentOwnerIsNotNull(user, pageable), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @JsonIgnore
    @JsonView(View.ExtendedPublic.class)
    @Operation(
            summary = "List of disks taken by the user",
            description = "Returns a page-by-page list of disks taken by the user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful get"),
            @ApiResponse(responseCode = "404", description = "User did not found by name")
    })
    @Transactional
    @RequestMapping(value = "/disks/occupied/{username}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Page<Disk>> getTakenDisksByUser(@PathVariable String username, Pageable pageable) {
        return userRepository.findByUsername(username)
                .map(user -> new ResponseEntity<>(diskRepository.findDiskByCurrentOwner(user, pageable), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
