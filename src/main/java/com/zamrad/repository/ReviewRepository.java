package com.zamrad.repository;

import com.zamrad.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    @Query("select r from Review r where r.profile.id = :artistId")
    List<Review> getAllReviews(@Param("artistId") UUID artistId);
}
