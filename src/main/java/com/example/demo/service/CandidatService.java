package com.example.demo.service;

import com.example.demo.Repository.CandidatRepository;
import com.example.demo.Repository.CandidatureRepository;
import com.example.demo.Repository.EntretienRepository;
import com.example.demo.model.Candidat;
import com.example.demo.model.Candidature;
import com.example.demo.model.Entretien;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CandidatService {

    private final CandidatRepository candidatRepository;
    private final CandidatureRepository candidatureRepository;
    private final EntretienRepository entretienRepository;

    public Candidat getCandidatByEmail(String email) {
        return candidatRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Candidat introuvable avec cet email : " + email));
    }


    public List<Candidature> getCandidaturesByCandidat(Candidat candidat) {
        return candidatureRepository.findByCandidat(candidat);
    }

    public List<Entretien> getEntretiensAVenir(Candidat candidat) {
        return entretienRepository.findByCandidature_CandidatAndDateEntretienAfterOrderByDateEntretienAsc(
                candidat, LocalDateTime.now());
    }

    public long countEntretiensAVenir(Candidat candidat) {
        return entretienRepository.countByCandidature_CandidatAndDateEntretienAfter(
                candidat, LocalDateTime.now());
    }
}
