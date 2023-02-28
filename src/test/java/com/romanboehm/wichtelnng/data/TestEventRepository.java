package com.romanboehm.wichtelnng.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TestEventRepository extends JpaRepository<Event, UUID> {

    @Query("""
                SELECT DISTINCT e FROM Event e
                LEFT JOIN FETCH e.participants
            """)
    List<Event> findAllWithParticipants();

    @Query("""
                SELECT DISTINCT e FROM Event e
                LEFT JOIN FETCH e.participants
                WHERE e.id = :eventId
            """)
    Optional<Event> findByIdWithParticipants(UUID eventId);
}
