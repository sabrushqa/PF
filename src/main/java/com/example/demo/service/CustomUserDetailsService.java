package com.example.demo.service;

import com.example.demo.Repository.UserRepository;
import com.example.demo.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * Service personnalisé chargé de récupérer les détails de l'utilisateur
 * lors de l'authentification via Spring Security.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Méthode utilisée par Spring Security pour charger un utilisateur à partir de son email.
     *
     * @param email L'adresse email de l'utilisateur.
     * @return UserDetails contenant les informations nécessaires à l'authentification.
     * @throws UsernameNotFoundException si aucun utilisateur n'est trouvé avec cet email.
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Récupération de l'utilisateur sans charger d'autres entités liées (ex: Candidat ou Recruteur)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("❌ Utilisateur non trouvé avec l'email : " + email));

        log.info("✅ Authentification en cours pour : {} | Rôle = {}", user.getEmail(), user.getRole());

        // Construction de l'objet UserDetails avec les rôles
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
