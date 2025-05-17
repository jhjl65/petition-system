package com.example.petitions.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")  // <- Ключовий момент
@Data
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;
}
