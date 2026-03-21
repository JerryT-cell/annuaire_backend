package com.banafrance.annuaire.repository;

import com.banafrance.annuaire.model.Profile;
import com.banafrance.annuaire.model.ProfileVisibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    Optional<Profile> findByUserId(UUID userId);

    @Query("""
        SELECT p FROM Profile p
        WHERE p.visibility IN :visibilities
        AND (:city IS NULL OR LOWER(p.city) = LOWER(:city))
        AND (:country IS NULL OR LOWER(p.country) = LOWER(:country))
        AND (:occupation IS NULL OR LOWER(p.occupation) LIKE LOWER(CONCAT('%', :occupation, '%')))
        AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))
             OR LOWER(p.surname) LIKE LOWER(CONCAT('%', :name, '%')))
    """)
    Page<Profile> findFiltered(
            @Param("visibilities") List<ProfileVisibility> visibilities,
            @Param("city") String city,
            @Param("country") String country,
            @Param("occupation") String occupation,
            @Param("name") String name,
            Pageable pageable
    );
}
