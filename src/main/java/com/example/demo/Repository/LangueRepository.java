package com.example.demo.Repository;

import com.example.demo.model.Langue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LangueRepository extends JpaRepository<Langue, Long> {
    List<Langue> findByProfilId(Long profilId);
}
