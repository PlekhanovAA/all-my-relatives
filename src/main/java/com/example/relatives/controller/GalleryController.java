package com.example.relatives.controller;

import com.example.relatives.model.Relative;
import com.example.relatives.model.Role;
import com.example.relatives.model.User;
import com.example.relatives.repository.RelativeRepository;
import com.example.relatives.repository.UserRepository;
import org.springframework.core.io.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    private final UserRepository userRepository;
    private final RelativeRepository relativeRepository;

    public GalleryController(UserRepository userRepository, RelativeRepository relativeRepository) {
        this.userRepository = userRepository;
        this.relativeRepository = relativeRepository;
    }

    private Path getGalleryPath(User user) {
        return Paths.get("uploads", user.getUsername(), "gallery");
    }

    private User resolveTargetUser(User current) {
        return current.getRole() == Role.VIEWER ? current.getOwner() : current;
    }

    @GetMapping("/gallery")
    public String gallery(Model model, @AuthenticationPrincipal UserDetails ud) throws IOException {
        User me = userRepository.findByUsername(ud.getUsername()).orElseThrow();
        User galleryOwner = me.getOwner() != null ? me.getOwner() : me;

        Path userGalleryPath = Paths.get("uploads", galleryOwner.getUsername(), "gallery");
        if (!Files.exists(userGalleryPath)) {
            Files.createDirectories(userGalleryPath);
        }

        List<String> filenames = Files.list(userGalleryPath)
                .filter(Files::isRegularFile)
                .map(p -> p.getFileName().toString())
                .sorted()
                .toList();

        model.addAttribute("photos", filenames);
        model.addAttribute("galleryOwner", galleryOwner.getUsername());

        // админ? тогда подгрузим его родственников для модалки
        boolean isAdmin = me.getOwner() == null; // см. нашу логику: у админа owner == null
        if (isAdmin) {
            List<Relative> rels = relativeRepository.findByOwner(me);
            model.addAttribute("relatives", rels);
        }

        return "gallery";
    }


    @GetMapping("/gallery/grid")
    public String galleryGrid(Model model, Principal principal) throws IOException {
        User current = userRepository.findByUsername(principal.getName()).orElseThrow();
        User target  = current.getRole() == Role.VIEWER ? current.getOwner() : current;

        Path userGalleryPath = Paths.get("uploads", target.getUsername(), "gallery");
        if (!Files.exists(userGalleryPath)) {
            Files.createDirectories(userGalleryPath);
        }
        List<String> filenames = Files.list(userGalleryPath)
                .map(Path::getFileName)
                .map(Path::toString)
                .toList();

        model.addAttribute("photos", filenames);
        model.addAttribute("galleryOwner", target.getUsername()); // ⬅️ ВАЖНО
        return "gallery_grid";
    }


    // --- Загрузка файлов (только ADMIN) ---
    @PostMapping("/gallery/upload")
    public String handleUpload(@RequestParam("images") List<MultipartFile> files,
                               Principal principal) throws IOException {
        User current = userRepository.findByUsername(principal.getName()).orElseThrow();

        if (current.getRole() != Role.ADMIN) {
            throw new SecurityException("Только администратор может загружать фото");
        }

        Path userGalleryPath = getGalleryPath(current);
        if (!Files.exists(userGalleryPath)) {
            Files.createDirectories(userGalleryPath);
        }

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
                Path targetPath = userGalleryPath.resolve(filename);
                file.transferTo(targetPath);
            }
        }
        return "redirect:/gallery/grid";
    }

    // --- Удаление фото (только ADMIN) ---
    @PostMapping("/gallery/delete")
    public String deleteImage(@RequestParam("filename") String filename,
                              Principal principal) throws IOException {
        User current = userRepository.findByUsername(principal.getName()).orElseThrow();

        if (current.getRole() != Role.ADMIN) {
            throw new SecurityException("Только администратор может удалять фото");
        }

        Path filePath = getGalleryPath(current).resolve(filename);
        Files.deleteIfExists(filePath);

        return "redirect:/gallery/grid";
    }

    // --- Отдача файлов ---
    @GetMapping("/uploads/{username}/gallery/{filename:.+}")
    @ResponseBody
    public Resource serveImage(@PathVariable String username,
                               @PathVariable String filename) throws IOException {
        Path file = Paths.get("uploads", username, "gallery").resolve(filename);
        return new UrlResource(file.toUri());
    }
}


