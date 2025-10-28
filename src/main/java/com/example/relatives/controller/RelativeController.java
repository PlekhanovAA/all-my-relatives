package com.example.relatives.controller;

import com.example.relatives.model.Relative;
import com.example.relatives.repository.LocationRepository;
import com.example.relatives.service.RelativeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class RelativeController {

    private final RelativeService relativeService;
    private final LocationRepository locationRepository;

    public RelativeController(RelativeService relativeService,
                              LocationRepository locationRepository) {
        this.relativeService = relativeService;
        this.locationRepository = locationRepository;
    }

    @GetMapping("/")
    public String homeRedirect() {
        return "redirect:/relatives";
    }

    @GetMapping("/relatives")
    public String list(Model model) {
        model.addAttribute("relatives", relativeService.getAll());
        return "relatives";
    }

    // ➕ Добавление нового родственника
    @GetMapping("/relative/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addRelative(Model model) {
        model.addAttribute("relative", new Relative());
        model.addAttribute("locations", locationRepository.findAll());

        // ✅ все существующие родственники для выбора родственных связей
        model.addAttribute("relatives", relativeService.getAll());

        return "relative_form";
    }

    // ✏️ Редактирование существующего родственника
    @GetMapping("/relative/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editForm(@PathVariable UUID id, Model model) {
        Relative relative = relativeService.getById(id);
        model.addAttribute("relative", relative);
        model.addAttribute("locations", locationRepository.findAll());

        // ✅ фильтруем список родственников — исключаем самого себя
        List<Relative> others = relativeService.getAll().stream()
                .filter(r -> !r.getId().equals(id))
                .collect(Collectors.toList());
        model.addAttribute("relatives", others);

        return "relative_form";
    }

    // 💾 Сохранение
    @PostMapping("/relative/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveRelative(@ModelAttribute Relative relative) {
        relativeService.save(relative);
        return "redirect:/relatives";
    }

    // 🌍 Карта
    @GetMapping("/map")
    public String map() {
        return "map";
    }
}

