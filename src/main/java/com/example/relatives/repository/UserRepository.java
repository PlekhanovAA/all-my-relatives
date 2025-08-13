package com.example.relatives.repository;

import com.example.relatives.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findByOwner(User owner); // найти всех приглашённых этим админом
}
