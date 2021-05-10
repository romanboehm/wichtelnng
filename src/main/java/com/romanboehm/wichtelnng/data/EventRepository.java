package com.romanboehm.wichtelnng.data;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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

    @Query(
            nativeQuery = true,
            value = "SELECT * FROM event e " +
                    "WHERE (e.local_date_time AT TIME ZONE e.zone_id) <= now()"
    )
    List<Event> findAllByDeadlineBeforeNow(); // Extra query for participants is fine for now.
}
