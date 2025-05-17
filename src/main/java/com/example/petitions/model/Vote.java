package com.example.petitions.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Vote {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    private Petition petition;

    private String voterEmail;

    private LocalDateTime votedAt = LocalDateTime.now();
}
