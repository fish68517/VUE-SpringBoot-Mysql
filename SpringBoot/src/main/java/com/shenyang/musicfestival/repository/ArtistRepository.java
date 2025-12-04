package com.shenyang.musicfestival.repository;

import com.shenyang.musicfestival.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Artist entity
 */
@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

    List<Artist> findByIsLocalBand(Boolean isLocalBand);

}
