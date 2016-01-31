package com.zuehlke.carrera.comp.repository;

import com.zuehlke.carrera.comp.domain.TrainingApplication;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the TrainingApplication entity
 */
public interface TrainingApplicationRepository extends JpaRepository<TrainingApplication,Long> {

    TrainingApplication findOneByTeamNameAndSessionId(String teamName, Long sessionId );

}
