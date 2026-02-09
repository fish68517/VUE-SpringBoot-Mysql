package com.postgraduate.service;

import com.postgraduate.dto.SchoolStatsDTO;
import com.postgraduate.entity.UserProfile;
import com.postgraduate.repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    public SchoolStatsDTO getSchoolStats(Long schoolId) {
        List<UserProfile> profiles = favoriteRepository.findUserProfilesBySchoolId(schoolId);
        long total = profiles.size();
        
        SchoolStatsDTO stats = new SchoolStatsDTO();
        stats.setTotalFavorites(total);

        if (total == 0) {
            stats.setTierDistribution(new HashMap<>());
            stats.setCet4Distribution(new HashMap<>());
            return stats;
        }

        // 1. 统计本科层次 (Undergrad Tier)
        Map<String, Long> tierCounts = profiles.stream()
            .collect(Collectors.groupingBy(
                p -> p.getUndergradTier() != null ? p.getUndergradTier() : "其他/未填", 
                Collectors.counting()
            ));
            
        Map<String, Double> tierDist = new HashMap<>();
        tierCounts.forEach((k, v) -> tierDist.put(k, (double) v / total));
        stats.setTierDistribution(tierDist);

        // 2. 统计四级成绩 (CET4 Score)
        Map<String, Long> cet4Counts = profiles.stream()
            .collect(Collectors.groupingBy(p -> {
                Integer score = p.getCet4Score();
                if (score == null || score == 0) return "未填写";
                if (score < 425) return "425分以下";
                if (score >= 425 && score < 500) return "425-500分";
                if (score >= 500 && score < 600) return "500-600分";
                return "600分以上";
            }, Collectors.counting()));

        Map<String, Double> cet4Dist = new HashMap<>();
        cet4Counts.forEach((k, v) -> cet4Dist.put(k, (double) v / total));
        stats.setCet4Distribution(cet4Dist);

        return stats;
    }
}