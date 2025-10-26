package com.example.relatives.repository;

import com.example.relatives.model.Relative;
import com.example.relatives.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RelativeRepository extends JpaRepository<Relative, UUID> {
    List<Relative> findByOwner(User owner);
}