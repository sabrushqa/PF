package com.example.demo.Repository;

import com.example.demo.model.Profil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfilRepository extends JpaRepository<Profil, Long> {
    Optional<Profil> findByCandidatId(Long candidatId);

}

