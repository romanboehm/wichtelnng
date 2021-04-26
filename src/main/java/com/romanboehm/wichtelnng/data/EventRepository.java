package com.romanboehm.wichtelnng.data;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    @Override
    @EntityGraph(attributePaths = "participants", type = FETCH)
    List<Event> findAll();

    @Override
    @EntityGraph(attributePaths = "participants", type = FETCH)
    Optional<Event> findById(UUID id);

    List<Event> findAllByDeadlineBefore(Instant deadline);

    Optional<Event> findByIdAndDeadlineAfter(UUID id, Instant deadline);
}
