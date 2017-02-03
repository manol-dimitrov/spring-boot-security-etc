package com.zamrad.repository;

import com.zamrad.domain.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@Transactional
public interface GenreRepository extends JpaRepository<Genre, UUID> {
    Genre findByName(String name);
}
