package com.example.relatives.controller;

import com.example.relatives.dto.PhotoTagDto;
import com.example.relatives.model.PhotoTag;
import com.example.relatives.model.Relative;
import com.example.relatives.model.User;
import com.example.relatives.repository.PhotoTagRepository;
import com.example.relatives.repository.RelativeRepository;
import com.example.relatives.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/gallery/tags")
public class PhotoTagController {

    private final PhotoTagRepository tagRepo;
    private final RelativeRepository relativeRepo;
    private final UserRepository userRepo;

    public PhotoTagController(PhotoTagRepository tagRepo,
                              RelativeRepository relativeRepo,
                              UserRepository userRepo) {
        this.tagRepo = tagRepo;
        this.relativeRepo = relativeRepo;
        this.userRepo = userRepo;
    }

    @GetMapping("/{filename}")
    public List<PhotoTagDto> getTags(@AuthenticationPrincipal UserDetails userDetails,
                                     @PathVariable String filename,
                                     @RequestParam(required = false) String ownerName) {
        String effectiveOwner = (ownerName != null) ? ownerName : userDetails.getUsername();

        User owner = userRepo.findByUsername(effectiveOwner)
                .orElseThrow();

        return tagRepo.findByOwnerAndFilename(owner, filename).stream()
                .map(tag -> {
                    PhotoTagDto dto = new PhotoTagDto();
                    dto.setId(tag.getId());
                    dto.setRelativeId(tag.getRelative().getId());
                    dto.setRelativeName(tag.getRelative().getFirstName() + " " + tag.getRelative().getLastName());
                    dto.setFilename(tag.getFilename());
                    dto.setX(tag.getX());
                    dto.setY(tag.getY());
                    dto.setWidth(tag.getWidth());
                    dto.setHeight(tag.getHeight());
                    return dto;
                })
                .collect(Collectors.toList());
    }


    @PostMapping("/save")
    public PhotoTagDto saveTag(@AuthenticationPrincipal UserDetails userDetails,
                               @RequestBody PhotoTagDto dto) {
        User owner = userRepo.findByUsername(userDetails.getUsername())
                .orElseThrow();
        Relative relative = relativeRepo.findById(dto.getRelativeId())
                .orElseThrow();

        PhotoTag tag = new PhotoTag();
        tag.setOwner(owner);
        tag.setRelative(relative);
        tag.setFilename(dto.getFilename());
        tag.setX(dto.getX());
        tag.setY(dto.getY());
        tag.setWidth(dto.getWidth());
        tag.setHeight(dto.getHeight());

        PhotoTag saved = tagRepo.save(tag);

        dto.setId(saved.getId());
        dto.setRelativeName(relative.getFirstName() + " " + relative.getLastName());
        return dto;
    }
}
