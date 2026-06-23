package com.schemaforge.team.service.impl;

import com.schemaforge.common.exception.BadRequestException;
import com.schemaforge.team.dto.CreateInvitationRequest;
import com.schemaforge.team.dto.InvitationResponse;
import com.schemaforge.team.entity.Invitation;
import com.schemaforge.team.entity.InvitationStatus;
import com.schemaforge.team.entity.Team;
import com.schemaforge.team.entity.TeamMember;
import com.schemaforge.team.entity.TeamRole;
import com.schemaforge.team.exception.InvitationExpiredException;
import com.schemaforge.team.exception.InvitationNotFoundException;
import com.schemaforge.team.exception.TeamAccessDeniedException;
import com.schemaforge.team.mapper.InvitationMapper;
import com.schemaforge.team.repository.InvitationRepository;
import com.schemaforge.team.repository.TeamMemberRepository;
import com.schemaforge.team.service.TeamAuthHelper;
import com.schemaforge.team.service.InvitationService;
import com.schemaforge.user.entity.User;
import com.schemaforge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService {

    private final InvitationRepository invitationRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;
    private final InvitationMapper invitationMapper;
    private final TeamAuthHelper teamAuthHelper;

    @Override
    @Transactional
    public InvitationResponse createInvitation(User currentUser, UUID teamId, CreateInvitationRequest request) {
        teamAuthHelper.verifyTeamOwner(currentUser, teamId);
        Team team = teamAuthHelper.verifyTeamExists(teamId);

        // Check if role is valid for invitation (cannot invite as OWNER)
        if (request.role() == TeamRole.OWNER) {
            throw new BadRequestException("Cannot invite a member with OWNER role");
        }

        // Check if user is already a member
        Optional<User> invitedUserOpt = userRepository.findByEmailIgnoreCase(request.email());
        if (invitedUserOpt.isPresent()) {
            User invitedUser = invitedUserOpt.get();
            if (teamMemberRepository.existsByTeamIdAndUserId(teamId, invitedUser.getId())) {
                throw new BadRequestException("User is already a member of this team");
            }
        }

        // Check for existing pending invitation
        Optional<Invitation> existingOpt = invitationRepository.findByTeamIdAndEmailAndStatus(teamId, request.email(), InvitationStatus.PENDING);
        if (existingOpt.isPresent()) {
            Invitation existing = existingOpt.get();
            if (existing.getExpiresAt().isAfter(Instant.now())) {
                throw new BadRequestException("An active invitation is already pending for this email address");
            } else {
                existing.setStatus(InvitationStatus.EXPIRED);
                invitationRepository.save(existing);
            }
        }

        String token = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plus(7, ChronoUnit.DAYS);

        Invitation invitation = Invitation.builder()
                .team(team)
                .invitedBy(currentUser)
                .email(request.email().trim().toLowerCase())
                .role(request.role())
                .token(token)
                .status(InvitationStatus.PENDING)
                .expiresAt(expiresAt)
                .build();

        Invitation saved = invitationRepository.save(invitation);
        log.info("Invitation created for: {} to team: {} by user: {} (token: {})", request.email(), teamId, currentUser.getId(), token);
        return invitationMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvitationResponse> getInvitations(User currentUser, UUID teamId) {
        teamAuthHelper.verifyTeamAccess(currentUser, teamId);

        return invitationRepository.findByTeamId(teamId)
                .stream()
                .map(invitationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void acceptInvitation(User currentUser, String token) {
        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new InvitationNotFoundException(token));

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new BadRequestException("This invitation is no longer pending");
        }

        if (invitation.getExpiresAt().isBefore(Instant.now())) {
            invitation.setStatus(InvitationStatus.EXPIRED);
            invitationRepository.save(invitation);
            throw new InvitationExpiredException("This invitation has expired");
        }

        // Verify email match
        if (!invitation.getEmail().equalsIgnoreCase(currentUser.getEmail())) {
            throw new TeamAccessDeniedException("This invitation was sent to a different email address");
        }

        // Check if already a member just in case
        if (teamMemberRepository.existsByTeamIdAndUserId(invitation.getTeam().getId(), currentUser.getId())) {
            invitation.setStatus(InvitationStatus.ACCEPTED);
            invitationRepository.save(invitation);
            throw new BadRequestException("You are already a member of this team");
        }

        // Add user as a team member
        TeamMember member = TeamMember.builder()
                .team(invitation.getTeam())
                .user(currentUser)
                .role(invitation.getRole())
                .joinedAt(Instant.now())
                .build();

        teamMemberRepository.save(member);

        // Update invitation status
        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitationRepository.save(invitation);

        log.info("User: {} accepted invitation: {} for team: {}", currentUser.getId(), invitation.getId(), invitation.getTeam().getId());
    }

    @Override
    @Transactional
    public void rejectInvitation(User currentUser, String token) {
        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new InvitationNotFoundException(token));

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new BadRequestException("This invitation is no longer pending");
        }

        if (invitation.getExpiresAt().isBefore(Instant.now())) {
            invitation.setStatus(InvitationStatus.EXPIRED);
            invitationRepository.save(invitation);
            throw new InvitationExpiredException("This invitation has expired");
        }

        // Verify email match
        if (!invitation.getEmail().equalsIgnoreCase(currentUser.getEmail())) {
            throw new TeamAccessDeniedException("This invitation was sent to a different email address");
        }

        // Reject/revoke the invitation
        invitation.setStatus(InvitationStatus.REVOKED);
        invitationRepository.save(invitation);

        log.info("User: {} rejected invitation: {} for team: {}", currentUser.getId(), invitation.getId(), invitation.getTeam().getId());
    }
}
