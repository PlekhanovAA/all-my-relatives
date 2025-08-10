package com.example.relatives.controller;

import org.springframework.core.io.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@Controller
public class GalleryController {

    private final Path uploadDir = Paths.get("uploads");

    public GalleryController() throws IOException {
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
    }

    @GetMapping("/gallery")
    public String gallery(Model model) throws IOException {
        List<String> filenames = Files.list(uploadDir)
                .map(Path::getFileName)
                .map(Path::toString)
                .toList();
        model.addAttribute("photos", filenames);
        return "gallery";
    }

    @PostMapping("/gallery/upload")
    public String handleUpload(@RequestParam("images") List<MultipartFile> files) throws IOException {
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
                Path targetPath = uploadDir.resolve(filename);
                file.transferTo(targetPath);
            }
        }
        return "redirect:/gallery";
    }


    @GetMapping("/uploads/{filename:.+}")
    @ResponseBody
    public Resource serveImage(@PathVariable String filename) throws IOException {
        Path file = uploadDir.resolve(filename);
        return new UrlResource(file.toUri());
    }

    @PostMapping("/gallery/delete")
    public String deleteImage(@RequestParam("filename") String filename) throws IOException {
        Path filePath = uploadDir.resolve(filename);
        Files.deleteIfExists(filePath);
        return "redirect:/gallery";
    }

    @GetMapping("/gallery/grid")
    public String galleryGrid(Model model) throws IOException {
        List<String> filenames = Files.list(uploadDir)
                .map(Path::getFileName)
                .map(Path::toString)
                .toList();
        model.addAttribute("photos", filenames);
        return "gallery_grid";
    }

}
