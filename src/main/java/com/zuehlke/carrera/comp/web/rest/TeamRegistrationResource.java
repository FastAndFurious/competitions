package com.zuehlke.carrera.comp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.zuehlke.carrera.comp.domain.TeamRegistration;
import com.zuehlke.carrera.comp.repository.TeamRegistrationRepository;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing TeamRegistration.
 */
@RestController
@RequestMapping("/api")
public class TeamRegistrationResource {

    private final Logger log = LoggerFactory.getLogger(TeamRegistrationResource.class);

    @Inject
    private TeamRegistrationRepository teamRegistrationRepository;

    /**
     * POST  /teamRegistrations -> Create a new teamRegistration.
     */
    @RequestMapping(value = "/teamRegistrations",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody TeamRegistration teamRegistration) throws URISyntaxException {
        log.debug("REST request to save TeamRegistration : {}", teamRegistration);
        if (teamRegistration.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new teamRegistration cannot already have an ID").build();
        }
        if ( teamRegistration.getRegistrationTime() == null ) {
            teamRegistration.setRegistrationTime(new LocalDateTime());
        }
        teamRegistrationRepository.save(teamRegistration);
        return ResponseEntity.created(new URI("/api/teamRegistrations/" + teamRegistration.getId())).build();
    }

    /**
     * PUT  /teamRegistrations -> Updates an existing teamRegistration.
     */
    @RequestMapping(value = "/teamRegistrations",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody TeamRegistration teamRegistration) throws URISyntaxException {
        log.debug("REST request to update TeamRegistration : {}", teamRegistration);
        if (teamRegistration.getId() == null) {
            return create(teamRegistration);
        }
        teamRegistrationRepository.save(teamRegistration);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /teamRegistrations -> get all the teamRegistrations.
     */
    @RequestMapping(value = "/teamRegistrations",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<TeamRegistration> getAll() {
        log.debug("REST request to get all TeamRegistrations");
        return teamRegistrationRepository.findAll();
    }

    /**
     * GET  /teamRegistrations/:id -> get the "id" teamRegistration.
     */
    @RequestMapping(value = "/teamRegistrations/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TeamRegistration> get(@PathVariable Long id) {
        log.debug("REST request to get TeamRegistration : {}", id);
        return Optional.ofNullable(teamRegistrationRepository.findOne(id))
            .map(teamRegistration -> new ResponseEntity<>(
                teamRegistration,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /teamRegistrations/:id -> delete the "id" teamRegistration.
     */
    @RequestMapping(value = "/teamRegistrations/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete TeamRegistration : {}", id);
        teamRegistrationRepository.delete(id);
    }
}
