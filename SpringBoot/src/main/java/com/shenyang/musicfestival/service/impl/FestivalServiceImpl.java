package com.shenyang.musicfestival.service.impl;

import com.shenyang.musicfestival.dto.ArtistDTO;
import com.shenyang.musicfestival.dto.FestivalDTO;
import com.shenyang.musicfestival.dto.ScheduleDTO;
import com.shenyang.musicfestival.entity.Artist;
import com.shenyang.musicfestival.entity.Festival;
import com.shenyang.musicfestival.entity.Schedule;
import com.shenyang.musicfestival.repository.ArtistRepository;
import com.shenyang.musicfestival.repository.FestivalRepository;
import com.shenyang.musicfestival.repository.ScheduleRepository;
import com.shenyang.musicfestival.service.FestivalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of FestivalService
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FestivalServiceImpl implements FestivalService {

    private final FestivalRepository festivalRepository;
    private final ScheduleRepository scheduleRepository;
    private final ArtistRepository artistRepository;

    @Override
    public Optional<FestivalDTO> getFestivalById(Long id) {
        return festivalRepository.findById(id)
            .map(this::convertToDTO);
    }

    @Override
    public Optional<FestivalDTO> getCurrentFestival() {
        return festivalRepository.findByStatus("ongoing")
            .map(this::convertToDTO);
    }

    @Override
    public List<ScheduleDTO> getFestivalSchedule(Long festivalId) {
        List<Schedule> schedules = scheduleRepository.findByFestivalIdOrderByStartTimeAsc(festivalId);
        return schedules.stream()
            .map(this::convertScheduleToDTO)
            .collect(Collectors.toList());
    }

    private FestivalDTO convertToDTO(Festival festival) {
        return FestivalDTO.builder()
            .id(festival.getId())
            .name(festival.getName())
            .description(festival.getDescription())
            .startDate(festival.getStartDate())
            .endDate(festival.getEndDate())
            .location(festival.getLocation())
            .posterUrl(festival.getPosterUrl())
            .status(festival.getStatus())
            .build();
    }

    private ScheduleDTO convertScheduleToDTO(Schedule schedule) {
        Artist artist = artistRepository.findById(schedule.getArtistId()).orElse(null);
        ArtistDTO artistDTO = null;
        if (artist != null) {
            artistDTO = ArtistDTO.builder()
                .id(artist.getId())
                .name(artist.getName())
                .description(artist.getDescription())
                .imageUrl(artist.getImageUrl())
                .isLocalBand(artist.getIsLocalBand())
                .build();
        }

        return ScheduleDTO.builder()
            .id(schedule.getId())
            .festivalId(schedule.getFestivalId())
            .artistId(schedule.getArtistId())
            .stageName(schedule.getStageName())
            .startTime(schedule.getStartTime())
            .endTime(schedule.getEndTime())
            .artist(artistDTO)
            .build();
    }

}
