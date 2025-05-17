package com.example.petitions.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Petition {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    @Column(length = 3000)
    private String description;

    @Lob
    @Column(name = "image_data", columnDefinition = "BLOB")
    private byte[] imageData;

    private String imageType;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private Integer maxVotes = 100;  // 🔧 замінено int → Integer

    @Column(nullable = false)
    private LocalDate deadline;

    private String voteToken = java.util.UUID.randomUUID().toString();

    @ManyToOne
    private User creator;

    @OneToMany(mappedBy = "petition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();
}
