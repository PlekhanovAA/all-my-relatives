package com.example.relatives.repository;

import com.example.relatives.model.Photo;
import com.example.relatives.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByOwner(User owner);
    Optional<Photo> findByIdAndOwnerId(Long id, Long ownerId);
}

