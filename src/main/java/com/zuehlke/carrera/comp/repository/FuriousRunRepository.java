package com.zuehlke.carrera.comp.repository;

import com.zuehlke.carrera.comp.domain.Competition;
import com.zuehlke.carrera.comp.domain.FuriousRun;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the Competition entity.
 */
public interface FuriousRunRepository extends JpaRepository<FuriousRun,Long> {

    List<FuriousRun> findBySessionId(Long sessionId);
}
