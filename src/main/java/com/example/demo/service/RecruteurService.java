package com.example.demo.service;

import com.example.demo.model.Recruteur;
import com.example.demo.Repository.RecruteurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecruteurService {

    private final RecruteurRepository recruteurRepository;

    @Transactional
    public Recruteur getRecruteurByEmail(String email) {
        return recruteurRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Recruteur introuvable avec l'email : " + email));
    }
}
