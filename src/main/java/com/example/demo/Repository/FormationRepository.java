package com.example.demo.Repository;

import com.example.demo.model.Formation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormationRepository extends JpaRepository<Formation, Long> {
    List<Formation> findByProfilId(Long profilId);
}

