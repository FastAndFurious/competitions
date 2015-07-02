package com.zuehlke.carrera.comp.repository;

import com.zuehlke.carrera.comp.domain.RacingSession;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the RacingSession entity.
 */
public interface RacingSessionRepository extends JpaRepository<RacingSession,Long> {

    List<RacingSession> findByCompetition(String competitionName);
}
