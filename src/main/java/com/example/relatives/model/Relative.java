package com.example.relatives.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.*;

@Entity
@Table(name = "relative")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Relative {

    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @EqualsAndHashCode.Include
    private String firstName;

    private String lastName;

    private String middleName;

    private String gender; // "MALE", "FEMALE", "OTHER"

    private String birthDate;

    private String deathDate;

    private String occupation;

    @Column(length = 2000)
    private String biography;

    private String photoUrl;

    @ManyToOne
    @JoinColumn(name = "birth_place_id")
    private Location birthPlace;

    @ManyToOne
    @JoinColumn(name = "death_place_id")
    private Location deathPlace;

    @ManyToOne
    @JoinColumn(name = "current_location_id")
    private Location currentLocation;

    // 👇 Родственные связи
    @ManyToOne
    @JoinColumn(name = "father_id")
    private Relative father;

    @ManyToOne
    @JoinColumn(name = "mother_id")
    private Relative mother;

    @ManyToOne
    @JoinColumn(name = "spouse_id")
    private Relative spouse;

    @OneToMany(mappedBy = "father", cascade = CascadeType.ALL)
    private List<Relative> childrenFromFather = new ArrayList<>();

    @OneToMany(mappedBy = "mother", cascade = CascadeType.ALL)
    private List<Relative> childrenFromMother = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    private String treeNodeId;

    @ElementCollection
    @CollectionTable(name = "relative_custom_relations", joinColumns = @JoinColumn(name = "relative_id"))
    @MapKeyColumn(name = "relation_type")
    @Column(name = "related_person_id")
    private Map<String, UUID> customRelations = new HashMap<>();

}
