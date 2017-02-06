package com.zamrad.repository;

import com.zamrad.domain.profiles.Profile;
import com.zamrad.domain.events.Event;
import com.zamrad.domain.events.EventSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public interface EventRepository extends JpaRepository<Event, UUID> {
    /**
     * Retrieves the event to which the eventslot is associated
     * @param eventSlot
     * @return
     */
    Event findByEventSlots(EventSlot eventSlot);

    List<Event> findByProfile(Profile profile);
}
