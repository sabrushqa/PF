package com.example.demo.Repository;

import com.example.demo.model.Candidature;
import com.example.demo.model.Candidat;
import com.example.demo.model.Recruteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidatureRepository extends JpaRepository<Candidature, Long> {

    List<Candidature> findByCandidat(Candidat candidat);
    List<Candidature> findTop5ByOffre_RecruteurOrderByDateCandidatureDesc(Recruteur recruteur);
    List<Candidature> findByOffre_Recruteur(Recruteur recruteur);
    List<Candidature> findByOffre_RecruteurAndVueParRecruteurFalse(Recruteur recruteur);

    List<Candidature> findByStatut(String statut); // ✅ CORRECT

}
