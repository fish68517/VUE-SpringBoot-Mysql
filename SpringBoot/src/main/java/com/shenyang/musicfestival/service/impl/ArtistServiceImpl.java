package com.shenyang.musicfestival.service.impl;

import com.shenyang.musicfestival.entity.Artist;
import com.shenyang.musicfestival.repository.ArtistRepository;
import com.shenyang.musicfestival.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation for Artist operations
 */
@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {

    private final ArtistRepository artistRepository;

    @Override
    public List<Artist> getAllArtists() {
        return artistRepository.findAll();
    }

    @Override
    public Optional<Artist> getArtistById(Long id) {
        return artistRepository.findById(id);
    }

    @Override
    public List<Artist> getLocalBands() {
        return artistRepository.findByIsLocalBand(true);
    }

    @Override
    public List<Artist> getNonLocalArtists() {
        return artistRepository.findByIsLocalBand(false);
    }

}
