package com.example.demo.service;

import com.example.demo.Repository.*;
import com.example.demo.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProfilService {

    @Autowired private ProfilRepository profilRepository;
    @Autowired private CandidatRepository candidatRepository;
    @Autowired private ExperienceRepository experienceRepository;
    @Autowired private FormationRepository formationRepository;
    @Autowired private LangueRepository langueRepository;

    // === PROFIL ===
    public void sauvegarderProfil(Profil profil) {
        profilRepository.save(profil);
    }

    public Optional<Profil> getProfilByEmail(String email) {
        return candidatRepository.findByUserEmail(email)
                .flatMap(candidat -> profilRepository.findByCandidatId(candidat.getId()));
    }

    @Transactional
    public Profil creerProfil(Profil profil, String email) {
        Candidat candidat = candidatRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Candidat non trouvé avec l'email: " + email));

        if (profilRepository.findByCandidatId(candidat.getId()).isPresent()) {
            throw new RuntimeException("Le candidat possède déjà un profil");
        }

        profil.setCandidat(candidat);
        candidat.setProfil(profil);

        candidatRepository.save(candidat);
        return profilRepository.save(profil);
    }

    @Transactional
    public Profil modifierProfil(Profil updated, String email) {
        Candidat candidat = candidatRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Candidat non trouvé avec l'email: " + email));

        Profil profil = profilRepository.findByCandidatId(candidat.getId())
                .orElseThrow(() -> new RuntimeException("Profil non trouvé pour le candidat"));

        profil.setBio(updated.getBio());
        return profilRepository.save(profil);
    }

    // === EXPERIENCE ===

    public Optional<Experience> getExperienceById(Long id) {
        return experienceRepository.findById(id);
    }

    @Transactional
    public Experience ajouterExperience(Experience experience, String email) {
        Profil profil = getProfilByEmail(email)
                .orElseThrow(() -> new RuntimeException("Profil non trouvé pour l'utilisateur"));

        experience.setProfil(profil);
        return experienceRepository.save(experience);
    }

    @Transactional
    public Experience modifierExperience(Long id, Experience updated) {
        Experience experience = experienceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expérience non trouvée avec l'ID: " + id));

        experience.setPoste(updated.getPoste());
        experience.setEntreprise(updated.getEntreprise());
        experience.setLieu(updated.getLieu());
        experience.setDateDebut(updated.getDateDebut());
        experience.setDateFin(updated.getDateFin());
        experience.setDescription(updated.getDescription());

        return experienceRepository.save(experience);
    }

    @Transactional
    public void supprimerExperience(Long id) {
        Experience experience = experienceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expérience non trouvée avec l'ID: " + id));

        experience.getProfil().getExperiences().remove(experience);
        experienceRepository.delete(experience);
    }

    // === FORMATION ===

    public Optional<Formation> getFormationById(Long id) {
        return formationRepository.findById(id);
    }

    @Transactional
    public Formation ajouterFormation(Formation formation, String email) {
        Profil profil = getProfilByEmail(email)
                .orElseThrow(() -> new RuntimeException("Profil non trouvé pour l'utilisateur"));

        formation.setProfil(profil);
        return formationRepository.save(formation);
    }

    @Transactional
    public Formation modifierFormation(Long id, Formation updated) {
        Formation formation = formationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formation non trouvée avec l'ID: " + id));

        formation.setDiplome(updated.getDiplome());
        formation.setEtablissement(updated.getEtablissement());
        formation.setLieu(updated.getLieu());
        formation.setDateDebut(updated.getDateDebut());
        formation.setDateFin(updated.getDateFin());
        formation.setDescription(updated.getDescription());

        return formationRepository.save(formation);
    }

    @Transactional
    public void supprimerFormation(Long id) {
        Formation formation = formationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formation non trouvée avec l'ID: " + id));

        formation.getProfil().getFormations().remove(formation);
        formationRepository.delete(formation);
    }

    // === LANGUE ===

    public Optional<Langue> getLangueById(Long id) {
        return langueRepository.findById(id);
    }

    @Transactional
    public Langue ajouterLangue(Langue langue, String email) {
        Profil profil = getProfilByEmail(email)
                .orElseThrow(() -> new RuntimeException("Profil non trouvé pour l'utilisateur"));

        langue.setProfil(profil);
        return langueRepository.save(langue);
    }

    @Transactional
    public Langue modifierLangue(Long id, Langue updated) {
        Langue langue = langueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Langue non trouvée avec l'ID: " + id));

        langue.setNom(updated.getNom());
        langue.setNiveau(updated.getNiveau());

        return langueRepository.save(langue);
    }

    @Transactional
    public void supprimerLangue(Long id) {
        Langue langue = langueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Langue non trouvée avec l'ID: " + id));

        langue.getProfil().getLangues().remove(langue);
        langueRepository.delete(langue);
    }
    public void modifierBio(String email, String bio) {
        Profil profil = getProfilByEmail(email)
                .orElseThrow(() -> new RuntimeException("Profil non trouvé"));
        profil.setBio(bio);
        sauvegarderProfil(profil);
    }

}
