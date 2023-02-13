package com.romanboehm.wichtelnng.usecases.notify;

import com.romanboehm.wichtelnng.data.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
interface NotifyRepository extends JpaRepository<Event, UUID> {

    @Override
    @Query("""
                SELECT e FROM Event e
                LEFT JOIN FETCH e.participants
                WHERE e.id = :id
            """)
    Optional<Event> findById(UUID id);

}
