package com.example.demo.Repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Trouver un utilisateur par son email
    Optional<User> findByEmail(String email);

    // Vérifier si un utilisateur existe avec cet email
    boolean existsByEmail(String email);

    // Trouver un utilisateur avec sa relation "Candidat" (si présente)
    @EntityGraph(attributePaths = {"candidat"})
    Optional<User> findWithCandidatByEmail(String email);

    // Trouver un utilisateur avec sa relation "Recruteur" (si présente)
    @EntityGraph(attributePaths = {"recruteur"})
    Optional<User> findWithRecruteurByEmail(String email);

    // Trouver un utilisateur avec ses relations "Candidat" et "Recruteur"
    @EntityGraph(attributePaths = {"candidat", "recruteur"})
    Optional<User> findWithRolesByEmail(String email);

    // Vérifier si un utilisateur existe avec un jeton de réinitialisation
    boolean existsByResetToken(String resetToken);

    // Trouver un utilisateur en fonction de son jeton de réinitialisation
    Optional<User> findByResetToken(String resetToken);
}
