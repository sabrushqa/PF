package com.example.demo.Repository;

import com.example.demo.model.Favori;
import com.example.demo.model.Offre;
import com.example.demo.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriRepository extends JpaRepository<Favori, Long> {
        boolean existsByCandidatAndOffre(User candidat, Offre offre);
        List<Favori> findByCandidat(User candidat);
        void deleteByCandidatAndOffre(User candidat, Offre offre);

    Optional<Favori> findByCandidatAndOffre(User candidat, Offre offre); // ✅ Ajoute cette ligne
    }


