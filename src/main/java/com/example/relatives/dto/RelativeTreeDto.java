package com.example.relatives.dto;

import com.example.relatives.model.Relative;

import java.util.UUID;

public record RelativeTreeDto(
        UUID id,
        String firstName,
        String lastName,
        String gender,
        String birthDate,
        String deathDate,
        UUID fatherId,
        UUID motherId,
        UUID spouseId
) {
    public static RelativeTreeDto fromEntity(Relative r) {
        return new RelativeTreeDto(
                r.getId(),
                r.getFirstName(),
                r.getLastName(),
                r.getGender(),
                r.getBirthDate(),
                r.getDeathDate(),
                r.getFather() != null ? r.getFather().getId() : null,
                r.getMother() != null ? r.getMother().getId() : null,
                r.getSpouse() != null ? r.getSpouse().getId() : null
        );
    }
}
