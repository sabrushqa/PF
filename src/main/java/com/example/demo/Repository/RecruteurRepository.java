package com.example.demo.Repository;

import com.example.demo.model.Recruteur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecruteurRepository extends JpaRepository<Recruteur, Long> {
    Optional<Recruteur> findByUserEmail(String email);
}
