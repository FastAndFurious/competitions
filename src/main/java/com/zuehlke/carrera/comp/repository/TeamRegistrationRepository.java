package com.zuehlke.carrera.comp.repository;

import com.zuehlke.carrera.comp.domain.TeamRegistration;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the TeamRegistration entity.
 */
public interface TeamRegistrationRepository extends JpaRepository<TeamRegistration,Long> {

}
