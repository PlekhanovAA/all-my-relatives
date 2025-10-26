package com.example.relatives.service;

import com.example.relatives.model.Relative;
import com.example.relatives.model.Role;
import com.example.relatives.model.User;
import com.example.relatives.repository.RelativeRepository;
import com.example.relatives.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class RelativeService {

    private final RelativeRepository relativeRepository;
    private final UserRepository userRepo;

    public RelativeService(RelativeRepository relativeRepository, UserRepository userRepo) {
        this.relativeRepository = relativeRepository;
        this.userRepo = userRepo;
    }

    public List<Relative> getAll() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (currentUser.getRole() == Role.ADMIN) {
            // Админ — видит только своих родственников
            return relativeRepository.findByOwner(currentUser);
        } else {
            // VIEWER — видит родственников владельца (админа)
            if (currentUser.getOwner() == null) {
                return List.of(); // нет владельца — пусто
            }
            return relativeRepository.findByOwner(currentUser.getOwner());
        }
    }

    public Relative getById(UUID id) {
        return relativeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Родственник не найден"));
    }

    public void save(Relative relative) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        relative.setOwner(currentUser);
        relativeRepository.save(relative);
    }
}


