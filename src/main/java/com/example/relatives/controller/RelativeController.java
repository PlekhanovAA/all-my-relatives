package com.example.relatives.controller;

import com.example.relatives.model.Relative;
import com.example.relatives.service.RelativeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RelativeController {

    private final RelativeService service;

    public RelativeController(RelativeService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String homeRedirect() {
        return "redirect:/relatives";
    }

    @GetMapping("/relatives")
    public String list(Model model) {
        model.addAttribute("relatives", service.getAll());
        return "relatives";
    }

    @GetMapping("/relative/add")
    public String addForm(Model model) {
        model.addAttribute("relative", new Relative());
        return "relative_form";
    }

    @GetMapping("/relative/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("relative", service.getById(id));
        return "relative_form";
    }

    @PostMapping("/relative/save")
    public String save(@ModelAttribute Relative relative) {
        service.save(relative);
        return "redirect:/relatives";
    }

//    @GetMapping("/gallery") public String gallery() { return "gallery"; }
    @GetMapping("/map") public String map() { return "map"; }
    @GetMapping("/tree") public String tree() { return "tree"; }
}
