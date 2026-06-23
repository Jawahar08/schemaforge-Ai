package com.schemaforge.team.repository;

import com.schemaforge.team.entity.Invitation;
import com.schemaforge.team.entity.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, UUID> {

    Optional<Invitation> findByToken(String token);

    List<Invitation> findByTeamId(UUID teamId);

    Optional<Invitation> findByTeamIdAndEmailAndStatus(UUID teamId, String email, InvitationStatus status);
}
