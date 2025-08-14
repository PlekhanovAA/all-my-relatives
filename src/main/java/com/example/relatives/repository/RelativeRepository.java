package com.example.relatives.repository;

import com.example.relatives.model.Relative;
import com.example.relatives.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RelativeRepository extends JpaRepository<Relative, Long> {
    List<Relative> findByOwner(User owner);
}