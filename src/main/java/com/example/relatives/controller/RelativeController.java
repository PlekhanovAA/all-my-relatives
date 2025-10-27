package com.example.relatives.controller;

import com.example.relatives.model.Relative;
import com.example.relatives.repository.LocationRepository;
import com.example.relatives.service.RelativeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

    @GetMapping("/relative/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addRelative(Model model) {
        model.addAttribute("relative", new Relative());
        model.addAttribute("locations", locationRepository.findAll());
        return "relative_form";
    }

    @GetMapping("/relative/edit/{id}")
    public String editForm(@PathVariable UUID id, Model model) {
        model.addAttribute("relative", relativeService.getById(id));
        model.addAttribute("locations", locationRepository.findAll());
        return "relative_form";
    }

    @PostMapping("/relative/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveRelative(@ModelAttribute Relative relative) {
        relativeService.save(relative);
        return "redirect:/relatives";
    }

//    @GetMapping("/gallery") public String gallery() { return "gallery"; }
    @GetMapping("/map") public String map() { return "map"; }
    @GetMapping("/tree") public String tree() { return "tree"; }
}
