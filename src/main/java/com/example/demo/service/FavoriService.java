package com.example.demo.service;


import com.example.demo.Repository.FavoriRepository;
import com.example.demo.model.Favori;
import com.example.demo.model.Offre;
import com.example.demo.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
    @RequiredArgsConstructor
    public class FavoriService {

        private final FavoriRepository favoriRepository;

        public void ajouterFavori(User candidat, Offre offre) {
            if (!favoriRepository.existsByCandidatAndOffre(candidat, offre)) {
                Favori favori = new Favori();
                favori.setCandidat(candidat);
                favori.setOffre(offre);
                favoriRepository.save(favori);
            }
        }


    @Transactional
    public void supprimerFavori(User candidat, Offre offre) {
        Favori favori = favoriRepository.findByCandidatAndOffre(candidat, offre)
                .orElseThrow(() -> new RuntimeException("Favori non trouvé"));
        favoriRepository.delete(favori);
    }

        public List<Favori> getFavoris(User candidat) {
            return favoriRepository.findByCandidat(candidat);
        }
    }


