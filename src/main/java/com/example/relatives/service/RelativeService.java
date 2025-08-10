package com.example.relatives.service;

import com.example.relatives.model.Relative;
import com.example.relatives.repository.RelativeRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RelativeService {
    private final RelativeRepository repo;

    public RelativeService(RelativeRepository repo) {
        this.repo = repo;
    }

    public List<Relative> getAll() { return repo.findAll(); }
    public Relative getById(Long id) { return repo.findById(id).orElseThrow(); }
    public void save(Relative r) { repo.save(r); }
}
