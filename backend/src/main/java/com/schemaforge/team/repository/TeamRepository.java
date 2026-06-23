package com.schemaforge.team.repository;

import com.schemaforge.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {

    Optional<Team> findBySlug(String slug);

    @Query("SELECT t FROM Team t WHERE t.owner.id = :userId OR EXISTS (SELECT tm FROM TeamMember tm WHERE tm.team.id = t.id AND tm.user.id = :userId)")
    List<Team> findAllForUser(@Param("userId") UUID userId);
}
