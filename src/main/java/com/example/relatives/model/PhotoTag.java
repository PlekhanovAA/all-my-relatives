package com.example.relatives.model;

import jakarta.persistence.*;

import lombok.Data;

@Entity
@Data
public class PhotoTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename; // имя фото внутри галереи
    private int x;
    private int y;
    private int width;
    private int height;

    @ManyToOne
    private User owner; // Владелец (админ)

    @ManyToOne
    private Relative relative; // Привязанный родственник
}
