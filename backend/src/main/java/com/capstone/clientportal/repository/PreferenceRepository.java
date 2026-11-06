package com.capstone.clientportal.repository;
import com.capstone.clientportal.entity.RankedPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface PreferenceRepository extends JpaRepository<RankedPreference, Long> {
    List<RankedPreference> findByUserIdAndTermOrderByRankOrderAsc(Long userId, String term);
    void deleteByUserIdAndTerm(Long userId, String term);
}
