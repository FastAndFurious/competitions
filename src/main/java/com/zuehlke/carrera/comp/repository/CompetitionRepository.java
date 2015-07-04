package com.zuehlke.carrera.comp.repository;

import com.zuehlke.carrera.comp.domain.Competition;
import org.springframework.data.jpa.repository.*;

/**
 * Spring Data JPA repository for the Competition entity.
 */
public interface CompetitionRepository extends JpaRepository<Competition,Long> {

    Competition findByName ( String compName );
}
