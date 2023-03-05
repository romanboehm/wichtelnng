package com.romanboehm.wichtelnng.usecases.createevent;

import com.romanboehm.wichtelnng.common.data.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
interface CreateEventRepository extends JpaRepository<Event, UUID> {
}
