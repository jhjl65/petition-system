package com.example.petitions.repository;

import com.example.petitions.model.Petition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetitionRepository extends JpaRepository<Petition, Long> {
    List<Petition> findAllByOrderByCreatedAtDesc();  // якщо потрібно
    Page<Petition> findAll(Pageable pageable);
}
