package com.example.relatives.controller;

import com.example.relatives.dto.RelativeTreeDto;
import com.example.relatives.repository.RelativeRepository;
import com.example.relatives.service.RelativeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class TreeController {

    private final RelativeService relativeService;
    private final RelativeRepository relativeRepository;

    public TreeController(RelativeService relativeService, RelativeRepository relativeRepository) {
        this.relativeService = relativeService;
        this.relativeRepository = relativeRepository;
    }

    // 🌳 Страница с древом (визуализация)
    @GetMapping("/tree")
    public String treePage(Model model) {
        // Просто рендерим HTML, JS сам подтянет данные через AJAX
        return "tree";
    }

    // 🔄 API для получения структуры древа в JSON
    @GetMapping("/api/tree")
    @ResponseBody
    public List<RelativeTreeDto> getTreeData() {
        return relativeRepository.findAll().stream()
                .map(RelativeTreeDto::fromEntity)
                .toList();
    }
}
