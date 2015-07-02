package com.zuehlke.carrera.comp.repository;

import com.zuehlke.carrera.comp.domain.Competition;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Competition entity.
 */
public interface CompetitionRepository extends JpaRepository<Competition,Long> {

}
