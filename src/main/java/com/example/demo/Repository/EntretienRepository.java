package com.example.demo.Repository;

import com.example.demo.model.Candidature;
import com.example.demo.model.Entretien;
import com.example.demo.model.Recruteur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EntretienRepository extends JpaRepository<Entretien, Long> {
    List<Entretien> findByCandidature_Candidat(com.example.demo.model.Candidat candidat);
    List<Entretien> findByCandidature_Offre_Recruteur(Recruteur recruteur);
    List<Entretien> findTop5ByCandidature_Offre_RecruteurOrderByDateEntretienDesc(Recruteur recruteur);
    Optional<Entretien> findByCandidature(Candidature candidature);
    List<Entretien> findByCandidature_Offre_RecruteurAndDateEntretienAfterAndVuParRecruteurFalse(
            Recruteur recruteur, LocalDateTime date);


}
