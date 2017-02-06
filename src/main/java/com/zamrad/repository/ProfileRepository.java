package com.zamrad.repository;

import com.zamrad.domain.profiles.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    Profile findByFacebookId(Long facebookId);
    List<Profile> findByProfileType(String profileType);
}
