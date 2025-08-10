package com.example.relatives.repository;

import com.example.relatives.model.Relative;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelativeRepository extends JpaRepository<Relative, Long> {
}