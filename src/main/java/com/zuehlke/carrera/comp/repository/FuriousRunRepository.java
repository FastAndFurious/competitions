package com.zuehlke.carrera.comp.repository;

import com.zuehlke.carrera.comp.domain.FuriousRun;
import com.zuehlke.carrera.comp.domain.RunStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the Competition entity.
 */
public interface FuriousRunRepository extends JpaRepository<FuriousRun,Long> {

    List<FuriousRun> findBySessionId(Long sessionId);

    FuriousRun findOneBySessionIdAndTeam(Long sessionId, String team);

    List<FuriousRun> findByStatus(RunStatus ongoing);
}
