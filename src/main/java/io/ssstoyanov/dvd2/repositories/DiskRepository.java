package io.ssstoyanov.dvd2.repositories;

import io.ssstoyanov.dvd2.entities.Disk;
import io.ssstoyanov.dvd2.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiskRepository extends MongoRepository<Disk, Long> {
    Page<Disk> findDiskByOriginalOwnerIsNull(Pageable pageable);

    Page<Disk> findDiskByCurrentOwnerIsNull(Pageable pageable);

    Page<Disk> findDiskByOriginalOwner(User user, Pageable pageable);

    Page<Disk> findDiskByOriginalOwnerAndCurrentOwnerIsNotNull(User user, Pageable pageable);

    Page<Disk> findDiskByCurrentOwner(User user, Pageable pageable);

    Optional<Disk> findByName(String name);
}
