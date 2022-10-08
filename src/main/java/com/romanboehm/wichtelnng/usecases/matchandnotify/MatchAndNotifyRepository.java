package com.romanboehm.wichtelnng.usecases.matchandnotify;

import com.romanboehm.wichtelnng.data.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
interface MatchAndNotifyRepository extends JpaRepository<Event, UUID> {

    @Query("""
                SELECT DISTINCT e FROM Event e
                LEFT JOIN FETCH e.participants
                WHERE e.deadline.instant <= :instant
            """)
    List<Event> findAllByDeadlineBefore(Instant instant);

}
