package com.example.relatives.controller;

import com.example.relatives.dto.PhotoDto;
import com.example.relatives.model.Photo;
import com.example.relatives.model.Relative;
import com.example.relatives.model.Role;
import com.example.relatives.model.User;
import com.example.relatives.repository.PhotoRepository;
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
    private final PhotoRepository photoRepository;

    public GalleryController(UserRepository userRepository,
                             RelativeRepository relativeRepository,
                             PhotoRepository photoRepository) {
        this.userRepository = userRepository;
        this.relativeRepository = relativeRepository;
        this.photoRepository = photoRepository;
    }

    private Path getGalleryPath(User user) {
        return Paths.get("uploads", user.getId().toString(), "gallery");
    }

    @GetMapping("/gallery")
    public String gallery(Model model,
                          @AuthenticationPrincipal UserDetails userDetails) {
        User current = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        User target = current.getRole() == Role.VIEWER ? current.getOwner() : current;

        List<Photo> photos = photoRepository.findByOwner(target);

        List<Map<String, Object>> photoDtos = photos.stream()
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", p.getId());
                    map.put("filename", p.getFilename());
                    return map;
                })
                .toList();

        List<Relative> relatives = relativeRepository.findByOwner(target);

        model.addAttribute("photos", photoDtos);
        model.addAttribute("galleryOwner", target.getId());
        model.addAttribute("relatives", relatives);

        return "gallery";
    }



    @GetMapping("/gallery/grid")
    public String galleryGrid(Model model, Principal principal) throws IOException {
        User current = userRepository.findByUsername(principal.getName()).orElseThrow();
        User target  = current.getRole() == Role.VIEWER ? current.getOwner() : current;

        Path userGalleryPath = getGalleryPath(target);
        if (!Files.exists(userGalleryPath)) {
            Files.createDirectories(userGalleryPath);
        }

        List<PhotoDto> photos = photoRepository.findByOwner(target).stream()
                .map(p -> new PhotoDto(p.getId(), p.getFilename()))
                .toList();

        model.addAttribute("photos", photos);
        model.addAttribute("galleryOwner", target.getId());
        return "gallery_grid";
    }

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
                String original = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
                String ext = original.contains(".") ? original.substring(original.lastIndexOf(".")) : "";
                String filename = UUID.randomUUID() + ext;

                Path targetPath = userGalleryPath.resolve(filename);
                file.transferTo(targetPath);

                // сохраняем запись в БД
                Photo photo = new Photo();
                photo.setOwner(current);
                photo.setFilename(filename);
                photoRepository.save(photo);
            }
        }
        return "redirect:/gallery/grid";
    }

    @PostMapping("/gallery/delete")
    public String deleteImage(@RequestParam("photoId") UUID photoId,
                              Principal principal) throws IOException {
        User current = userRepository.findByUsername(principal.getName()).orElseThrow();

        if (current.getRole() != Role.ADMIN) {
            throw new SecurityException("Только администратор может удалять фото");
        }

        Photo photo = photoRepository.findById(photoId).orElseThrow();
        Path filePath = getGalleryPath(current).resolve(photo.getFilename());
        Files.deleteIfExists(filePath);

        photoRepository.delete(photo);

        return "redirect:/gallery/grid";
    }

    @GetMapping("/uploads/{userId}/gallery/{filename:.+}")
    @ResponseBody
    public Resource serveImage(@PathVariable UUID userId,
                               @PathVariable String filename) throws IOException {
        Path file = Paths.get("uploads", userId.toString(), "gallery").resolve(filename);
        return new UrlResource(file.toUri());
    }
}




