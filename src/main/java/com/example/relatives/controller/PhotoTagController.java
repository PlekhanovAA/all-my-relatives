package com.example.relatives.controller;

import com.example.relatives.dto.PhotoTagDto;
import com.example.relatives.model.*;
import com.example.relatives.repository.PhotoRepository;
import com.example.relatives.repository.PhotoTagRepository;
import com.example.relatives.repository.RelativeRepository;
import com.example.relatives.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/gallery/tags")
public class PhotoTagController {
    private final PhotoTagRepository tagRepo;
    private final PhotoRepository photoRepo;
    private final RelativeRepository relativeRepo;
    private final UserRepository userRepo; // 👈 добавляем

    public PhotoTagController(PhotoTagRepository tagRepo,
                              PhotoRepository photoRepo,
                              RelativeRepository relativeRepo,
                              UserRepository userRepo) {
        this.tagRepo = tagRepo;
        this.photoRepo = photoRepo;
        this.relativeRepo = relativeRepo;
        this.userRepo = userRepo;
    }

    // 📌 Получение всех меток для фото
    @GetMapping("/{photoId}")
    public List<PhotoTagDto> getTags(@AuthenticationPrincipal UserDetails userDetails,
                                     @PathVariable Long photoId) {
        User current = userRepo.findByUsername(userDetails.getUsername()).orElseThrow();
        User target = current.getRole() == Role.VIEWER ? current.getOwner() : current;

        // 🔥 Проверяем по ownerId, а не по equals
        Photo photo = photoRepo.findByIdAndOwnerId(photoId, target.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return tagRepo.findByPhoto(photo).stream()
                .map(tag -> {
                    PhotoTagDto dto = new PhotoTagDto();
                    dto.setId(tag.getId());
                    dto.setRelativeId(tag.getRelative().getId());
                    dto.setRelativeName(tag.getRelative().getFirstName() + " " + tag.getRelative().getLastName());
                    dto.setPhotoId(photo.getId());
                    dto.setX(tag.getX());
                    dto.setY(tag.getY());
                    dto.setWidth(tag.getWidth());
                    dto.setHeight(tag.getHeight());
                    return dto;
                })
                .toList();
    }

    // 📌 Сохранение новой метки (только ADMIN)
    @PostMapping("/save")
    public PhotoTagDto saveTag(@AuthenticationPrincipal UserDetails userDetails,
                               @RequestBody PhotoTagDto dto) {
        User current = userRepo.findByUsername(userDetails.getUsername()).orElseThrow();
        if (current.getRole() != Role.ADMIN) {
            throw new SecurityException("Добавлять метки может только админ");
        }

        Photo photo = photoRepo.findByIdAndOwnerId(dto.getPhotoId(), current.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Relative relative = relativeRepo.findById(dto.getRelativeId()).orElseThrow();

        PhotoTag tag = new PhotoTag();
        tag.setPhoto(photo);
        tag.setRelative(relative);
        tag.setX(dto.getX());
        tag.setY(dto.getY());
        tag.setWidth(dto.getWidth());
        tag.setHeight(dto.getHeight());

        PhotoTag saved = tagRepo.save(tag);

        PhotoTagDto result = new PhotoTagDto();
        result.setId(saved.getId());
        result.setRelativeId(relative.getId());
        result.setRelativeName(relative.getFirstName() + " " + relative.getLastName());
        result.setPhotoId(photo.getId());
        result.setX(saved.getX());
        result.setY(saved.getY());
        result.setWidth(saved.getWidth());
        result.setHeight(saved.getHeight());

        return result;
    }
}
