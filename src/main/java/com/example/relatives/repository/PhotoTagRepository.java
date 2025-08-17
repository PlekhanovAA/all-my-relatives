package com.example.relatives.repository;

import com.example.relatives.model.PhotoTag;
import com.example.relatives.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhotoTagRepository extends JpaRepository<PhotoTag, Long> {
    List<PhotoTag> findByOwnerAndFilename(User owner, String filename);
}
