package com.schemaforge.activity.repository;

import com.schemaforge.activity.entity.Activity;
import com.schemaforge.activity.entity.ActivityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {

    // ── Actor feed ────────────────────────────────────────────────────────────

    @Query("""
            SELECT a FROM Activity a
            WHERE a.actor.id = :userId
              AND (:activityType IS NULL OR a.activityType = :activityType)
            """)
    Page<Activity> findByActorFiltered(
            @Param("userId") UUID userId,
            @Param("activityType") ActivityType activityType,
            Pageable pageable
    );

    @Query("""
            SELECT a FROM Activity a
            WHERE a.actor.id = :userId
              AND (:activityType IS NULL OR a.activityType = :activityType)
              AND a.createdAt >= :from
            """)
    Page<Activity> findByActorFilteredFrom(
            @Param("userId") UUID userId,
            @Param("activityType") ActivityType activityType,
            @Param("from") Instant from,
            Pageable pageable
    );

    @Query("""
            SELECT a FROM Activity a
            WHERE a.actor.id = :userId
              AND (:activityType IS NULL OR a.activityType = :activityType)
              AND a.createdAt <= :to
            """)
    Page<Activity> findByActorFilteredTo(
            @Param("userId") UUID userId,
            @Param("activityType") ActivityType activityType,
            @Param("to") Instant to,
            Pageable pageable
    );

    @Query("""
            SELECT a FROM Activity a
            WHERE a.actor.id = :userId
              AND (:activityType IS NULL OR a.activityType = :activityType)
              AND a.createdAt >= :from
              AND a.createdAt <= :to
            """)
    Page<Activity> findByActorFilteredBetween(
            @Param("userId") UUID userId,
            @Param("activityType") ActivityType activityType,
            @Param("from") Instant from,
            @Param("to") Instant to,
            Pageable pageable
    );

    // ── Project feed ──────────────────────────────────────────────────────────

    @Query("""
            SELECT a FROM Activity a
            WHERE a.projectId = :projectId
              AND (:activityType IS NULL OR a.activityType = :activityType)
            """)
    Page<Activity> findByProjectFiltered(
            @Param("projectId") UUID projectId,
            @Param("activityType") ActivityType activityType,
            Pageable pageable
    );

    @Query("""
            SELECT a FROM Activity a
            WHERE a.projectId = :projectId
              AND (:activityType IS NULL OR a.activityType = :activityType)
              AND a.createdAt >= :from
            """)
    Page<Activity> findByProjectFilteredFrom(
            @Param("projectId") UUID projectId,
            @Param("activityType") ActivityType activityType,
            @Param("from") Instant from,
            Pageable pageable
    );

    @Query("""
            SELECT a FROM Activity a
            WHERE a.projectId = :projectId
              AND (:activityType IS NULL OR a.activityType = :activityType)
              AND a.createdAt <= :to
            """)
    Page<Activity> findByProjectFilteredTo(
            @Param("projectId") UUID projectId,
            @Param("activityType") ActivityType activityType,
            @Param("to") Instant to,
            Pageable pageable
    );

    @Query("""
            SELECT a FROM Activity a
            WHERE a.projectId = :projectId
              AND (:activityType IS NULL OR a.activityType = :activityType)
              AND a.createdAt >= :from
              AND a.createdAt <= :to
            """)
    Page<Activity> findByProjectFilteredBetween(
            @Param("projectId") UUID projectId,
            @Param("activityType") ActivityType activityType,
            @Param("from") Instant from,
            @Param("to") Instant to,
            Pageable pageable
    );

    // ── Team feed ─────────────────────────────────────────────────────────────

    @Query("""
            SELECT a FROM Activity a
            WHERE a.teamId = :teamId
              AND (:activityType IS NULL OR a.activityType = :activityType)
            """)
    Page<Activity> findByTeamFiltered(
            @Param("teamId") UUID teamId,
            @Param("activityType") ActivityType activityType,
            Pageable pageable
    );

    @Query("""
            SELECT a FROM Activity a
            WHERE a.teamId = :teamId
              AND (:activityType IS NULL OR a.activityType = :activityType)
              AND a.createdAt >= :from
            """)
    Page<Activity> findByTeamFilteredFrom(
            @Param("teamId") UUID teamId,
            @Param("activityType") ActivityType activityType,
            @Param("from") Instant from,
            Pageable pageable
    );

    @Query("""
            SELECT a FROM Activity a
            WHERE a.teamId = :teamId
              AND (:activityType IS NULL OR a.activityType = :activityType)
              AND a.createdAt <= :to
            """)
    Page<Activity> findByTeamFilteredTo(
            @Param("teamId") UUID teamId,
            @Param("activityType") ActivityType activityType,
            @Param("to") Instant to,
            Pageable pageable
    );

    @Query("""
            SELECT a FROM Activity a
            WHERE a.teamId = :teamId
              AND (:activityType IS NULL OR a.activityType = :activityType)
              AND a.createdAt >= :from
              AND a.createdAt <= :to
            """)
    Page<Activity> findByTeamFilteredBetween(
            @Param("teamId") UUID teamId,
            @Param("activityType") ActivityType activityType,
            @Param("from") Instant from,
            @Param("to") Instant to,
            Pageable pageable
    );

    // ── Schema feed ───────────────────────────────────────────────────────────

    @Query("""
            SELECT a FROM Activity a
            WHERE a.schemaId = :schemaId
            """)
    Page<Activity> findBySchemaId(
            @Param("schemaId") UUID schemaId,
            Pageable pageable
    );
}