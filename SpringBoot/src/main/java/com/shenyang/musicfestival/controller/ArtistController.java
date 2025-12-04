package com.shenyang.musicfestival.controller;

import com.shenyang.musicfestival.dto.ArtistDTO;
import com.shenyang.musicfestival.entity.Artist;
import com.shenyang.musicfestival.service.ArtistService;
import com.shenyang.musicfestival.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for Artist operations
 */
@RestController
@RequestMapping("/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;

    /**
     * Get all artists
     * GET /artists
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ArtistDTO>>> getAllArtists() {
        List<Artist> artists = artistService.getAllArtists();
        List<ArtistDTO> artistDTOs = artists.stream()
            .map(this::convertToDTO)
            .toList();
        return ResponseEntity.ok(ApiResponse.success(artistDTOs, "获取艺人列表成功"));
    }

    /**
     * Get artist by ID
     * GET /artists/:id
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ArtistDTO>> getArtistById(@PathVariable Long id) {
        var artist = artistService.getArtistById(id);
        if (artist.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("艺人不存在"));
        }
        return ResponseEntity.ok(ApiResponse.success(convertToDTO(artist.get()), "获取艺人详情成功"));
    }

    /**
     * Get all local bands
     * GET /artists/local-bands
     */
    @GetMapping("/local-bands")
    public ResponseEntity<ApiResponse<List<ArtistDTO>>> getLocalBands() {
        List<Artist> localBands = artistService.getLocalBands();
        List<ArtistDTO> artistDTOs = localBands.stream()
            .map(this::convertToDTO)
            .toList();
        return ResponseEntity.ok(ApiResponse.success(artistDTOs, "获取沈阳本土乐队列表成功"));
    }

    /**
     * Convert Artist entity to ArtistDTO
     */
    private ArtistDTO convertToDTO(Artist artist) {
        return ArtistDTO.builder()
            .id(artist.getId())
            .name(artist.getName())
            .description(artist.getDescription())
            .imageUrl(artist.getImageUrl())
            .isLocalBand(artist.getIsLocalBand())
            .build();
    }

}
