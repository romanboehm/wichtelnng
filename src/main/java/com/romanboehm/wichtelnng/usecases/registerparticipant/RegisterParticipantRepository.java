package com.romanboehm.wichtelnng.usecases.registerparticipant;

import com.romanboehm.wichtelnng.common.data.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
interface RegisterParticipantRepository extends JpaRepository<Event, UUID> {

    @Query("""
                select distinct e from Event e
                left join fetch e.participants
                where e.id = :eventId
            """)
    Optional<Event> findByIdWithParticipants(UUID eventId);

    @Query(nativeQuery = true, value = """
            select
                (case when count(p.*) > 0 then true else false end)
            from participant p
            where p.event_id = :eventId and p.name = :pName and p.email = :pEmail
            """)
    boolean eventContainsParticipant(@Param("eventId") UUID eventId, @Param("pName") String pName, @Param("pEmail") String pEmail);
}
