package com.example.demo.service;

import com.example.demo.Repository.CandidatRepository;
import com.example.demo.Repository.CandidatureRepository;
import com.example.demo.model.Candidat;
import com.example.demo.model.Candidature;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CandidatService {

    private final CandidatRepository candidatRepository;
    private final CandidatureRepository candidatureRepository;

    public Candidat getCandidatByEmail(String email) {
        return candidatRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Candidat introuvable avec cet email : " + email));
    }


    public List<Candidature> getCandidaturesByCandidat(Candidat candidat) {
        return candidatureRepository.findByCandidat(candidat);
    }
}
