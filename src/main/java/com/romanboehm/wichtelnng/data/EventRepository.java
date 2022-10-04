package com.romanboehm.wichtelnng.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    @Override
    @Query("""
                SELECT DISTINCT e FROM Event e
                LEFT JOIN FETCH e.participants
            """)
    List<Event> findAll();

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

    @Query("""
                SELECT DISTINCT e FROM Event e
                LEFT JOIN FETCH e.participants
                WHERE e.deadline.instant <= :instant
            """)
    List<Event> findAllByDeadlineBefore(Instant instant);
}
