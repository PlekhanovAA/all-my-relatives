package com.example.relatives.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class PhotoTagDto {
    private UUID id;
    private UUID photoId;
    private UUID relativeId;
    private String relativeName;

    private double x;
    private double y;
    private double width;
    private double height;
}

