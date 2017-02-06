package com.zamrad.repository;

import com.zamrad.domain.profiles.Profile;
import com.zamrad.domain.events.EventSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public interface EventSlotRepository extends JpaRepository<EventSlot, UUID> {
    /**
     * Retrieves a list of event slots artist has pitched for
     * @param profile
     * @return
     */
    List<EventSlot> findByPitchers(Profile profile);
}
