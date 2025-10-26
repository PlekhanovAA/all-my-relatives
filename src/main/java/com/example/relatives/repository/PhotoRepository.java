package com.example.relatives.repository;

import com.example.relatives.model.Photo;
import com.example.relatives.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PhotoRepository extends JpaRepository<Photo, UUID> {
    List<Photo> findByOwner(User owner);
    Optional<Photo> findByIdAndOwnerId(UUID id, UUID ownerId);
}

