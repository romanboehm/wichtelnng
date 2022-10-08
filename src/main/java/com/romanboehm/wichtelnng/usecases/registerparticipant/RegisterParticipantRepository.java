package com.romanboehm.wichtelnng.usecases.registerparticipant;

import com.romanboehm.wichtelnng.data.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
interface RegisterParticipantRepository extends JpaRepository<Event, UUID> {

    @Override
    @Query("""
                SELECT DISTINCT e FROM Event e
                LEFT JOIN FETCH e.participants
                WHERE e.id = :id
            """)
    Optional<Event> findById(UUID id);

    @Query("""
                SELECT DISTINCT e FROM Event e
                LEFT JOIN FETCH e.participants
                WHERE e.id = :id AND e.deadline.instant > :instant
            """)
    Optional<Event> findByIdAndDeadlineAfter(UUID id, Instant instant);
}
