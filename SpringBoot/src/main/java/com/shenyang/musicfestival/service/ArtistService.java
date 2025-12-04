package com.shenyang.musicfestival.service;

import com.shenyang.musicfestival.entity.Artist;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Artist operations
 */
public interface ArtistService {

    /**
     * Get all artists
     */
    List<Artist> getAllArtists();

    /**
     * Get artist by ID
     */
    Optional<Artist> getArtistById(Long id);

    /**
     * Get all local bands
     */
    List<Artist> getLocalBands();

    /**
     * Get all non-local artists
     */
    List<Artist> getNonLocalArtists();

}
