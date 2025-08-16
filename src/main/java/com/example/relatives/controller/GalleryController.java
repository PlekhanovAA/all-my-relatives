package com.example.relatives.controller;

import org.springframework.core.io.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.security.Principal;
import java.util.*;

@Controller
public class GalleryController {

    private Path getUserGalleryDir() throws IOException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Path userGalleryPath = Paths.get("uploads", username, "gallery");

        if (!Files.exists(userGalleryPath)) {
            Files.createDirectories(userGalleryPath);
        }
        return userGalleryPath;
    }

    @GetMapping("/gallery")
    public String gallery(Model model) throws IOException {
        Path userGalleryPath = getUserGalleryDir();

        List<String> filenames = Files.list(userGalleryPath)
                .map(Path::getFileName)
                .map(Path::toString)
                .toList();

        model.addAttribute("photos", filenames);
        return "gallery";
    }

    @PostMapping("/gallery/upload")
    public String handleUpload(@RequestParam("images") List<MultipartFile> files) throws IOException {
        Path userGalleryPath = getUserGalleryDir();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String filename = StringUtils.cleanPath(
                        Objects.requireNonNull(file.getOriginalFilename())
                );
                Path targetPath = userGalleryPath.resolve(filename);
                file.transferTo(targetPath);
            }
        }
        return "redirect:/gallery";
    }

    @GetMapping("/uploads/{username}/gallery/{filename:.+}")
    @ResponseBody
    public Resource serveImage(@PathVariable String username,
                               @PathVariable String filename) throws IOException {
        Path file = Paths.get("uploads", username, "gallery").resolve(filename);
        return new UrlResource(file.toUri());
    }

    @PostMapping("/gallery/delete")
    public String deleteImage(@RequestParam("filename") String filename) throws IOException {
        Path userGalleryPath = getUserGalleryDir();
        Path filePath = userGalleryPath.resolve(filename);
        Files.deleteIfExists(filePath);
        return "redirect:/gallery";
    }

    @GetMapping("/gallery/grid")
    public String galleryGrid(Model model) throws IOException {
        Path userGalleryPath = getUserGalleryDir();

        List<String> filenames = Files.list(userGalleryPath)
                .map(Path::getFileName)
                .map(Path::toString)
                .toList();

        model.addAttribute("photos", filenames);
        return "gallery_grid";
    }
}


