package com.example.relatives.dto;

import lombok.Data;

@Data
public class PhotoTagDto {
    private Long id;
    private Long photoId;
    private Long relativeId;
    private String relativeName;

    private int x;
    private int y;
    private int width;
    private int height;
}

