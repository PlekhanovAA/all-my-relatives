package com.example.relatives.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;      // имя файла в папке
    private String originalName;  // оригинальное имя при загрузке

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
}
