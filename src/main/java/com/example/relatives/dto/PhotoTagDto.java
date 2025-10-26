package com.example.relatives.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class PhotoTagDto {
    private UUID id;
    private UUID photoId;
    private UUID relativeId;
    private String relativeName;

    private int x;
    private int y;
    private int width;
    private int height;
}

