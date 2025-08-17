package com.example.relatives.dto;

import lombok.Data;

@Data
public class PhotoTagDto {
    private Long id;
    private Long relativeId;
    private String relativeName;
    private String filename;
    private int x;
    private int y;
    private int width;
    private int height;
}
