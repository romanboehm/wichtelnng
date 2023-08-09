package com.romanboehm.wichtelnng.utils;

import com.romanboehm.wichtelnng.common.data.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TestEventRepository extends JpaRepository<Event, UUID> {

    @Query("""
                select distinct e from Event e
                left join fetch e.participants
                where e.id = :eventId
            """)
    Optional<Event> findByIdWithParticipants(UUID eventId);
}
