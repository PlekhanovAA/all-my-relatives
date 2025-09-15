package com.example.relatives.repository;

import com.example.relatives.model.Photo;
import com.example.relatives.model.PhotoTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhotoTagRepository extends JpaRepository<PhotoTag, Long> {
    List<PhotoTag> findByPhoto(Photo photo);
}